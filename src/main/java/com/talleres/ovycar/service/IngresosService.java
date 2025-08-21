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
        
        // Filtrar mantenimientos válidos (con fecha programada y costo)
        List<Mantenimiento> mantenimientosValidos = mantenimientos.stream()
                .filter(m -> m.getFechaProgramada() != null && m.getCosto() != null)
                .collect(Collectors.toList());

        if (mantenimientosValidos.isEmpty()) {
            return new HistorialSemanasDTO(new ArrayList<>(), 0.0, 0.0, 0.0);
        }

        // Debug: Imprimir información de cada mantenimiento
        System.out.println("=== DEBUG: MANTENIMIENTOS Y SUS SEMANAS ===");
        for (Mantenimiento m : mantenimientosValidos) {
            String semana = getSemanaFromDate(m);
            System.out.println("Mantenimiento ID: " + m.getId() + 
                             ", Fecha Programada: " + m.getFechaProgramada() + 
                             ", Semana calculada: " + semana);
        }
        System.out.println("==========================================");

        // Agrupar por semana usando fecha_programada
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

        return new HistorialSemanasDTO(resumenesSemanales, totalGeneral, promedioSemanal, crecimientoPromedio);
    }

    public ResumenSemanalDTO getResumenSemana(String semana) {
        // Obtener mantenimientos de la semana específica
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll().stream()
                .filter(m -> m.getFechaProgramada() != null && m.getCosto() != null)
                .filter(m -> getSemanaFromDate(m).equals(semana))
                .collect(Collectors.toList());

        return crearResumenSemanal(semana, mantenimientos);
    }

    public List<IngresoMantenimientoDTO> getIngresosPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener mantenimientos en el rango de fechas
        List<Mantenimiento> mantenimientos = mantenimientoRepository.findAll().stream()
                .filter(m -> m.getFechaProgramada() != null && m.getCosto() != null)
                .filter(m -> {
                    LocalDate fechaMantenimiento = m.getFechaProgramada().toLocalDate();
                    return !fechaMantenimiento.isBefore(fechaInicio) && !fechaMantenimiento.isAfter(fechaFin);
                })
                .collect(Collectors.toList());

        return mantenimientos.stream()
                .map(this::convertirAIngresoMantenimientoDTO)
                .collect(Collectors.toList());
    }

    private ResumenSemanalDTO crearResumenSemanal(String semana, List<Mantenimiento> mantenimientos) {
        if (mantenimientos.isEmpty()) {
            return new ResumenSemanalDTO(semana, null, null, 0.0, 0, 0.0, 0.0, new ArrayList<>());
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

        // Convertir mantenimientos a DTOs
        List<IngresoMantenimientoDTO> mantenimientosDTO = mantenimientos.stream()
                .map(this::convertirAIngresoMantenimientoDTO)
                .collect(Collectors.toList());

        return new ResumenSemanalDTO(semana, fechaInicio, fechaFin, totalIngresos, 
                                   cantidadMantenimientos, promedioPorMantenimiento, 
                                   crecimientoVsSemanaAnterior, mantenimientosDTO);
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
        
        String semana = mantenimiento.getFechaProgramada() != null ? 
                       getSemanaFromDate(mantenimiento) : "";

        return new IngresoMantenimientoDTO(
            mantenimiento.getId(),
            mantenimiento.getFechaProgramada().toLocalDate(),
            concepto,
            mantenimiento.getCosto(),
            cliente,
            vehiculo,
            mecanico,
            estado,
            semana
        );
    }

    private String getSemanaFromDate(Mantenimiento mantenimiento) {
        if (mantenimiento.getFechaProgramada() == null) return "";
        
        LocalDate fecha = mantenimiento.getFechaProgramada().toLocalDate();
        int year = fecha.getYear();
        
        // Calcular la semana manualmente para domingo a domingo
        // Encontrar el primer domingo del año
        LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);
        LocalDate firstSunday = firstDayOfYear;
        while (firstSunday.getDayOfWeek().getValue() != 7) { // 7 = domingo
            firstSunday = firstSunday.plusDays(1);
        }
        
        // Si la fecha es anterior al primer domingo, pertenece a la semana 0 del año anterior
        if (fecha.isBefore(firstSunday)) {
            year = year - 1;
            LocalDate firstDayOfPreviousYear = LocalDate.of(year, 1, 1);
            LocalDate firstSundayOfPreviousYear = firstDayOfPreviousYear;
            while (firstSundayOfPreviousYear.getDayOfWeek().getValue() != 7) {
                firstSundayOfPreviousYear = firstSundayOfPreviousYear.plusDays(1);
            }
            firstSunday = firstSundayOfPreviousYear;
        }
        
        // Calcular la diferencia en días desde el primer domingo
        long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(firstSunday, fecha);
        int week = (int) (daysDiff / 7) + 1;
        
        return String.format("%d-%02d", year, week);
    }

    private LocalDate[] getFechasSemana(String semana) {
        try {
            String[] parts = semana.split("-");
            int year = Integer.parseInt(parts[0]);
            int week = Integer.parseInt(parts[1]);
            
            // Encontrar el primer domingo del año
            LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);
            LocalDate firstSunday = firstDayOfYear;
            while (firstSunday.getDayOfWeek().getValue() != 7) { // 7 = domingo
                firstSunday = firstSunday.plusDays(1);
            }
            
            // Calcular el domingo de la semana especificada
            LocalDate sunday = firstSunday.plusWeeks(week - 1);
            LocalDate nextSunday = sunday.plusDays(7);
            
            return new LocalDate[]{sunday, nextSunday.minusDays(1)};
        } catch (Exception e) {
            // En caso de error, retornar la semana actual (domingo a domingo)
            LocalDate now = LocalDate.now();
            LocalDate firstDayOfYear = LocalDate.of(now.getYear(), 1, 1);
            LocalDate firstSunday = firstDayOfYear;
            while (firstSunday.getDayOfWeek().getValue() != 7) {
                firstSunday = firstSunday.plusDays(1);
            }
            
            long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(firstSunday, now);
            int currentWeek = (int) (daysDiff / 7) + 1;
            
            LocalDate sunday = firstSunday.plusWeeks(currentWeek - 1);
            LocalDate nextSunday = sunday.plusDays(7);
            
            return new LocalDate[]{sunday, nextSunday.minusDays(1)};
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
                // Calcular cuántas semanas tiene el año anterior
                LocalDate lastDayOfPreviousYear = LocalDate.of(yearAnterior, 12, 31);
                LocalDate firstDayOfPreviousYear = LocalDate.of(yearAnterior, 1, 1);
                LocalDate firstSundayOfPreviousYear = firstDayOfPreviousYear;
                while (firstSundayOfPreviousYear.getDayOfWeek().getValue() != 7) {
                    firstSundayOfPreviousYear = firstSundayOfPreviousYear.plusDays(1);
                }
                long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(firstSundayOfPreviousYear, lastDayOfPreviousYear);
                weekAnterior = (int) (daysDiff / 7) + 1;
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
}
