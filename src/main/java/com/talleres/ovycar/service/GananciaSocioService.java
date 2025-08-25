package com.talleres.ovycar.service;

import com.talleres.ovycar.dto.GananciaSocioMantenimientoDTO;
import com.talleres.ovycar.dto.ResumenSemanalSocioDTO;
import com.talleres.ovycar.dto.HistorialSemanasSocioDTO;
import com.talleres.ovycar.entity.Mantenimiento;
import com.talleres.ovycar.entity.Egreso;
import com.talleres.ovycar.repository.MantenimientoRepository;
import com.talleres.ovycar.repository.EgresoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GananciaSocioService {

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    @Autowired
    private EgresoRepository egresoRepository;

    public HistorialSemanasSocioDTO getHistorialSemanas() {
        // Obtener todos los mantenimientos
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll();
        
        // Filtrar mantenimientos válidos (con fecha de fin y costo)
        List<Mantenimiento> mantenimientosValidos = mantenimientos.stream()
                .filter(m -> m.getFechaFin() != null && m.getCosto() != null)
                .collect(Collectors.toList());

        if (mantenimientosValidos.isEmpty()) {
            return new HistorialSemanasSocioDTO(new ArrayList<>(), 0.0, 0.0, 0.0, 0.0);
        }

        // Agrupar por semana usando fecha_fin
        Map<String, List<Mantenimiento>> mantenimientosPorSemana = mantenimientosValidos.stream()
                .collect(Collectors.groupingBy(this::getSemanaFromDate));

        // Crear resúmenes semanales
        List<ResumenSemanalSocioDTO> resumenesSemanales = new ArrayList<>();
        
        for (Map.Entry<String, List<Mantenimiento>> entry : mantenimientosPorSemana.entrySet()) {
            String semana = entry.getKey();
            List<Mantenimiento> mantenimientosSemana = entry.getValue();
            
            ResumenSemanalSocioDTO resumen = crearResumenSemanal(semana, mantenimientosSemana);
            resumenesSemanales.add(resumen);
        }

        // Ordenar por semana (más reciente primero)
        resumenesSemanales.sort((a, b) -> b.getSemana().compareTo(a.getSemana()));

        // Calcular totales generales
        double totalIngresosNetos = resumenesSemanales.stream()
                .mapToDouble(r -> r.getIngresosNetos() != null ? r.getIngresosNetos() : 0.0)
                .sum();

        double totalEgresos = resumenesSemanales.stream()
                .mapToDouble(r -> r.getEgresos() != null ? r.getEgresos() : 0.0)
                .sum();

        double totalGananciaSocio = resumenesSemanales.stream()
                .mapToDouble(r -> r.getGananciaSocio() != null ? r.getGananciaSocio() : 0.0)
                .sum();

        double promedioSemanal = resumenesSemanales.isEmpty() ? 0 : totalGananciaSocio / resumenesSemanales.size();

        return new HistorialSemanasSocioDTO(resumenesSemanales, totalIngresosNetos, totalEgresos, totalGananciaSocio, promedioSemanal);
    }

    public ResumenSemanalSocioDTO getResumenSemana(String semana) {
        // Obtener mantenimientos de la semana específica
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll().stream()
                .filter(m -> m.getFechaFin() != null && m.getCosto() != null)
                .filter(m -> getSemanaFromDate(m).equals(semana))
                .collect(Collectors.toList());

        return crearResumenSemanal(semana, mantenimientos);
    }

    public List<GananciaSocioMantenimientoDTO> getGananciaPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener mantenimientos en el rango de fechas
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll().stream()
                .filter(m -> m.getFechaFin() != null && m.getCosto() != null)
                .filter(m -> {
                    LocalDate fechaMantenimiento = m.getFechaFin().toLocalDate();
                    return !fechaMantenimiento.isBefore(fechaInicio) && !fechaMantenimiento.isAfter(fechaFin);
                })
                .collect(Collectors.toList());

        return mantenimientos.stream()
                .map(this::convertirAGananciaSocioMantenimientoDTO)
                .collect(Collectors.toList());
    }



    private ResumenSemanalSocioDTO crearResumenSemanal(String semana, List<Mantenimiento> mantenimientos) {
        if (mantenimientos.isEmpty()) {
            return new ResumenSemanalSocioDTO(semana, null, null, 0.0, 0.0, 0.0, 0, new ArrayList<>());
        }

        // Calcular fechas de la semana
        LocalDate[] fechas = getFechasSemana(semana);
        LocalDate fechaInicio = fechas[0];
        LocalDate fechaFin = fechas[1];

        // Calcular ingresos netos
        double ingresosNetos = mantenimientos.stream()
                .mapToDouble(m -> calcularIngresoNeto(m))
                .sum();

        // Obtener egresos de la semana
        double egresos = obtenerEgresosSemana(fechaInicio, fechaFin);

        // Calcular ganancia por socio
        double gananciaSocio = ingresosNetos - egresos;

        int cantidadMantenimientos = mantenimientos.size();

        // Convertir mantenimientos a DTOs
        List<GananciaSocioMantenimientoDTO> mantenimientosDTO = mantenimientos.stream()
                .map(this::convertirAGananciaSocioMantenimientoDTO)
                .collect(Collectors.toList());

        return new ResumenSemanalSocioDTO(semana, fechaInicio, fechaFin, ingresosNetos, 
                                        egresos, gananciaSocio, cantidadMantenimientos, mantenimientosDTO);
    }

    private GananciaSocioMantenimientoDTO convertirAGananciaSocioMantenimientoDTO(Mantenimiento mantenimiento) {
        String concepto = mantenimiento.getTipoMantenimiento() != null ? 
                         mantenimiento.getTipoMantenimiento().toString() : "Mantenimiento";
        
        String cliente = mantenimiento.getVehiculo() != null && mantenimiento.getVehiculo().getCliente() != null ?
                        mantenimiento.getVehiculo().getCliente().getNombre() + " " + 
                        mantenimiento.getVehiculo().getCliente().getApellido() : "Cliente no especificado";
        
        String vehiculo = mantenimiento.getVehiculo() != null ?
                         mantenimiento.getVehiculo().getMarca() + " " + mantenimiento.getVehiculo().getModelo() + 
                         " - " + mantenimiento.getVehiculo().getPlaca() : "Vehículo no especificado";
        
        String mecanico = mantenimiento.getMecanico() != null ? mantenimiento.getMecanico() : "No asignado";
        
        String estado = mantenimiento.getEstado() != null ? 
                       mantenimiento.getEstado().toString().toLowerCase() : "pendiente";
        
        String semana = mantenimiento.getFechaFin() != null ? 
                       getSemanaFromDate(mantenimiento) : "";

        Double ingresoNeto = calcularIngresoNeto(mantenimiento);

        return new GananciaSocioMantenimientoDTO(
            mantenimiento.getId(),
            mantenimiento.getFechaFin().toLocalDate(),
            concepto,
            cliente,
            vehiculo,
            mecanico,
            estado,
            semana,
            ingresoNeto
        );
    }

    private double calcularIngresoNeto(Mantenimiento mantenimiento) {
        double ingresoNeto = 0.0;
        
        // 50% de mano de obra
        if (mantenimiento.getCostoManoObra() != null) {
            ingresoNeto += mantenimiento.getCostoManoObra() * 0.5;
        }
        
        // 20% de valor de repuestos (solo si hay valor de repuestos)
        if (mantenimiento.getValorRepuestos() != null && mantenimiento.getValorRepuestos() > 0) {
            ingresoNeto += mantenimiento.getValorRepuestos() * 0.2;
        }
        
        // Ingresos adicionales (costos adicionales)
        if (mantenimiento.getCostoAdicionales() != null) {
            ingresoNeto += mantenimiento.getCostoAdicionales();
        }
        
        return ingresoNeto;
    }

    private double obtenerEgresosSemana(LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener todos los egresos
        List<Egreso> todosLosEgresos = egresoRepository.findAll();
        
        // Filtrar egresos válidos
        List<Egreso> egresosValidos = todosLosEgresos.stream()
                .filter(e -> e.getFechaEgreso() != null)
                .collect(Collectors.toList());
        
        // Filtrar por rango de fechas
        List<Egreso> egresos = egresosValidos.stream()
                .filter(e -> {
                    LocalDate fechaEgreso = e.getFechaEgreso().toLocalDate();
                    return !fechaEgreso.isBefore(fechaInicio) && !fechaEgreso.isAfter(fechaFin);
                })
                .collect(Collectors.toList());

        return egresos.stream()
                .mapToDouble(e -> e.getMonto() != null ? e.getMonto().doubleValue() : 0.0)
                .sum();
    }

    private String getSemanaFromDate(Mantenimiento mantenimiento) {
        if (mantenimiento.getFechaFin() == null) return "";
        
        LocalDate fecha = mantenimiento.getFechaFin().toLocalDate();
        
        // Usar WeekFields.ISO para calcular la semana con lunes como primer día
        WeekFields weekFields = WeekFields.ISO;
        int year = fecha.get(weekFields.weekBasedYear());
        int week = fecha.get(weekFields.weekOfWeekBasedYear());
        
        return String.format("%d-%02d", year, week);
    }

    private LocalDate[] getFechasSemana(String semana) {
        try {
            String[] parts = semana.split("-");
            int year = Integer.parseInt(parts[0]);
            int week = Integer.parseInt(parts[1]);
            
            // Usar WeekFields.ISO para calcular las fechas de la semana
            WeekFields weekFields = WeekFields.ISO;
            
            // Encontrar el primer lunes del año
            LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);
            LocalDate firstMonday = firstDayOfYear;
            while (firstMonday.getDayOfWeek().getValue() != 1) {
                firstMonday = firstMonday.plusDays(1);
            }
            
            // Calcular el lunes de la semana especificada
            LocalDate startOfWeek = firstMonday.plusWeeks(week - 1);
            
            // El fin de la semana es el domingo (6 días después del lunes)
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            
            return new LocalDate[]{startOfWeek, endOfWeek};
        } catch (Exception e) {
            // En caso de error, retornar la semana actual
            LocalDate now = LocalDate.now();
            WeekFields weekFields = WeekFields.ISO;
            int currentYear = now.get(weekFields.weekBasedYear());
            int currentWeek = now.get(weekFields.weekOfWeekBasedYear());
            
            return getFechasSemana(String.format("%d-%02d", currentYear, currentWeek));
        }
    }
}
