package com.talleres.ovycar.service;

import com.talleres.ovycar.dto.IngresoMantenimientoDTO;
import com.talleres.ovycar.dto.ResumenSemanalDTO;
import com.talleres.ovycar.dto.HistorialSemanasDTO;
import com.talleres.ovycar.entity.Mantenimiento;
import com.talleres.ovycar.entity.Vehiculo;
import com.talleres.ovycar.entity.Cliente;
import com.talleres.ovycar.repository.MantenimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IngresosService {

    @Autowired
    private MantenimientoRepository mantenimientoRepository;

    public HistorialSemanasDTO getHistorialSemanas() {
        // Obtener todos los mantenimientos
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll();
        
        // Filtrar mantenimientos válidos (con fecha de fin y costo)
        List<Mantenimiento> mantenimientosValidos = mantenimientos.stream()
                .filter(m -> m.getFechaFin() != null && m.getCosto() != null)
                .collect(Collectors.toList());

        if (mantenimientosValidos.isEmpty()) {
            return new HistorialSemanasDTO(new ArrayList<>(), 0.0, 0.0, 0.0, 0.0);
        }

        // Debug: Imprimir información de cada mantenimiento
        System.out.println("=== DEBUG: MANTENIMIENTOS Y SUS SEMANAS ===");
        for (Mantenimiento m : mantenimientosValidos) {
            String semana = getSemanaFromDate(m);
            LocalDate fecha = m.getFechaFin().toLocalDate();
            
            // Calcular el rango de la semana para mostrar
            LocalDate[] fechasSemana = getFechasSemana(semana);
            LocalDate inicioSemana = fechasSemana[0];
            LocalDate finSemana = fechasSemana[1];
            
            System.out.println("Mantenimiento ID: " + m.getId() + 
                             ", Fecha Fin: " + m.getFechaFin() + 
                             ", Semana calculada: " + semana +
                             ", Rango semana: " + inicioSemana + " a " + finSemana +
                             ", Día de la semana: " + fecha.getDayOfWeek());
        }
        System.out.println("==========================================");

        // Agrupar por semana usando fecha_fin
        Map<String, List<Mantenimiento>> mantenimientosPorSemana = mantenimientosValidos.stream()
                .collect(Collectors.groupingBy(this::getSemanaFromDate));

        // Debug: Imprimir agrupación por semana
        System.out.println("=== DEBUG: AGRUPACIÓN POR SEMANA ===");
        for (Map.Entry<String, List<Mantenimiento>> entry : mantenimientosPorSemana.entrySet()) {
            System.out.println("Semana: " + entry.getKey() + ", Cantidad: " + entry.getValue().size());
        }
        System.out.println("=====================================");

        // Crear resúmenes semanales
        List<ResumenSemanalDTO> resumenesSemanales = new ArrayList<>();
        
        for (Map.Entry<String, List<Mantenimiento>> entry : mantenimientosPorSemana.entrySet()) {
            String semana = entry.getKey();
            List<Mantenimiento> mantenimientosSemana = entry.getValue();
            
            ResumenSemanalDTO resumen = crearResumenSemanal(semana, mantenimientosSemana);
            resumenesSemanales.add(resumen);
        }

        // Ordenar por semana (más reciente primero)
        resumenesSemanales.sort((a, b) -> b.getSemana().compareTo(a.getSemana()));

        // Calcular totales generales
        double totalGeneral = resumenesSemanales.stream()
                .mapToDouble(ResumenSemanalDTO::getTotalIngresos)
                .sum();

        double promedioSemanal = resumenesSemanales.isEmpty() ? 0 : totalGeneral / resumenesSemanales.size();

        // Calcular crecimiento promedio
        double crecimientoPromedio = calcularCrecimientoPromedio(resumenesSemanales);

        // Calcular total de ganancias netas
        double totalGananciasNetas = resumenesSemanales.stream()
                .mapToDouble(ResumenSemanalDTO::getGananciasNetas)
                .sum();

        return new HistorialSemanasDTO(resumenesSemanales, totalGeneral, promedioSemanal, crecimientoPromedio, totalGananciasNetas);
    }

    public ResumenSemanalDTO getResumenSemana(String semana) {
        // Obtener mantenimientos de la semana específica
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll().stream()
                .filter(m -> m.getFechaFin() != null && m.getCosto() != null)
                .filter(m -> getSemanaFromDate(m).equals(semana))
                .collect(Collectors.toList());

        return crearResumenSemanal(semana, mantenimientos);
    }

    public List<IngresoMantenimientoDTO> getIngresosPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener mantenimientos en el rango de fechas
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll().stream()
                .filter(m -> m.getFechaFin() != null && m.getCosto() != null)
                .filter(m -> {
                    LocalDate fechaMantenimiento = m.getFechaFin().toLocalDate();
                    return !fechaMantenimiento.isBefore(fechaInicio) && !fechaMantenimiento.isAfter(fechaFin);
                })
                .collect(Collectors.toList());

        return mantenimientos.stream()
                .map(this::convertirAIngresoMantenimientoDTO)
                .collect(Collectors.toList());
    }

    private ResumenSemanalDTO crearResumenSemanal(String semana, List<Mantenimiento> mantenimientos) {
        if (mantenimientos.isEmpty()) {
            return new ResumenSemanalDTO(semana, null, null, 0.0, 0, 0.0, 0.0, 0.0, new ArrayList<>());
        }

        // Calcular fechas de la semana
        LocalDate[] fechas = getFechasSemana(semana);
        LocalDate fechaInicio = fechas[0];
        LocalDate fechaFin = fechas[1];

        // Calcular totales
        double totalIngresos = mantenimientos.stream()
                .mapToDouble(m -> m.getCosto() != null ? m.getCosto() : 0.0)
                .sum();

        int cantidadMantenimientos = mantenimientos.size();
        double promedioPorMantenimiento = cantidadMantenimientos > 0 ? totalIngresos / cantidadMantenimientos : 0.0;

        // Calcular crecimiento vs semana anterior
        double crecimientoVsSemanaAnterior = calcularCrecimientoVsSemanaAnterior(semana, totalIngresos);

        // Calcular ganancias netas
        double gananciasNetas = calcularGananciasNetas(mantenimientos);

        // Convertir mantenimientos a DTOs
        List<IngresoMantenimientoDTO> mantenimientosDTO = mantenimientos.stream()
                .map(this::convertirAIngresoMantenimientoDTO)
                .collect(Collectors.toList());

        return new ResumenSemanalDTO(semana, fechaInicio, fechaFin, totalIngresos, 
                                   cantidadMantenimientos, promedioPorMantenimiento, 
                                   crecimientoVsSemanaAnterior, gananciasNetas, mantenimientosDTO);
    }

    private IngresoMantenimientoDTO convertirAIngresoMantenimientoDTO(Mantenimiento mantenimiento) {
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

        // Calcular valores del desglose
        Double valorRepuestos = mantenimiento.getValorRepuestos() != null ? mantenimiento.getValorRepuestos() : 0.0;
        Double pagoRepuestos = valorRepuestos > 0 ? valorRepuestos * 0.8 : 0.0; // 80% de repuestos
        Double porcentajeRepuestos = valorRepuestos > 0 ? valorRepuestos * 0.2 : 0.0; // 20% de repuestos
        Double pagoManoObra = mantenimiento.getCostoManoObra() != null ? mantenimiento.getCostoManoObra() * 0.5 : 0.0; // 50% de mano de obra
        Double gananciaManoObra = mantenimiento.getCostoManoObra() != null ? mantenimiento.getCostoManoObra() * 0.5 : 0.0; // 50% ganancia del taller
        Double ingresoAdicional = mantenimiento.getCostoAdicionales() != null ? mantenimiento.getCostoAdicionales() : 0.0;
        Double ingresoNeto = calcularIngresoNeto(mantenimiento);

        // Debug: Imprimir valores para verificar
        System.out.println("=== DEBUG: VALORES DEL MANTENIMIENTO ID " + mantenimiento.getId() + " ===");
        System.out.println("Costo: " + mantenimiento.getCosto());
        System.out.println("Costo Mano de Obra: " + mantenimiento.getCostoManoObra());
        System.out.println("Pago Mano de Obra (50%): " + pagoManoObra);
        System.out.println("Ganancia Mano de Obra (50%): " + gananciaManoObra);
        System.out.println("Valor Repuestos: " + mantenimiento.getValorRepuestos());
        System.out.println("Pago Repuestos (80%): " + pagoRepuestos);
        System.out.println("20% Repuestos: " + porcentajeRepuestos);
        System.out.println("Ingreso Adicional: " + ingresoAdicional);
        System.out.println("Ingreso Neto Calculado: " + ingresoNeto);
        System.out.println("================================================");

        return new IngresoMantenimientoDTO(
            mantenimiento.getId(),
            mantenimiento.getFechaFin().toLocalDate(),
            concepto,
            mantenimiento.getCosto(),
            cliente,
            vehiculo,
            mecanico,
            estado,
            semana,
            pagoManoObra,
            pagoRepuestos,
            porcentajeRepuestos,
            ingresoNeto,
            ingresoAdicional,
            gananciaManoObra
        );
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

    private double calcularCrecimientoVsSemanaAnterior(String semanaActual, double totalActual) {
        try {
            // Calcular semana anterior usando la misma lógica
            String[] parts = semanaActual.split("-");
            int year = Integer.parseInt(parts[0]);
            int week = Integer.parseInt(parts[1]);
            
            // Calcular semana anterior
            int weekAnterior = week - 1;
            int yearAnterior = year;
            
            // Si la semana anterior es 0, ir al año anterior
            if (weekAnterior <= 0) {
                yearAnterior = year - 1;
                // Calcular cuántas semanas tiene el año anterior usando WeekFields.ISO
                LocalDate lastDayOfPreviousYear = LocalDate.of(yearAnterior, 12, 31);
                WeekFields weekFields = WeekFields.ISO;
                weekAnterior = lastDayOfPreviousYear.get(weekFields.weekOfWeekBasedYear());
            }
            
            String semanaAnterior = String.format("%d-%02d", yearAnterior, weekAnterior);
            
            // Obtener total de la semana anterior
            ResumenSemanalDTO resumenAnterior = getResumenSemana(semanaAnterior);
            double totalAnterior = resumenAnterior.getTotalIngresos();
            
            if (totalAnterior == 0) return 0.0;
            
            return ((totalActual - totalAnterior) / totalAnterior) * 100;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double calcularCrecimientoPromedio(List<ResumenSemanalDTO> resumenes) {
        if (resumenes.size() < 2) return 0.0;
        
        double sumaCrecimientos = resumenes.stream()
                .mapToDouble(ResumenSemanalDTO::getCrecimientoVsSemanaAnterior)
                .sum();
        
        return sumaCrecimientos / (resumenes.size() - 1); // -1 porque el primer elemento no tiene semana anterior
    }

    private double calcularGananciasNetas(List<Mantenimiento> mantenimientos) {
        double gananciasNetas = 0.0;
        
        for (Mantenimiento mantenimiento : mantenimientos) {
            // 50% de mano de obra
            if (mantenimiento.getCostoManoObra() != null) {
                gananciasNetas += mantenimiento.getCostoManoObra() * 0.5;
            }
            
            // 20% de valor de repuestos (solo si hay valor de repuestos)
            if (mantenimiento.getValorRepuestos() != null && mantenimiento.getValorRepuestos() > 0) {
                gananciasNetas += mantenimiento.getValorRepuestos() * 0.2;
            }
            
            // Ingresos adicionales (costos adicionales)
            if (mantenimiento.getCostoAdicionales() != null) {
                gananciasNetas += mantenimiento.getCostoAdicionales();
            }
        }
        
        return gananciasNetas;
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
}
