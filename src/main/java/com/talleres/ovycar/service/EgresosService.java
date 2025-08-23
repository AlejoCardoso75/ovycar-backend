package com.talleres.ovycar.service;

import com.talleres.ovycar.dto.EgresoSemanalDTO;
import com.talleres.ovycar.dto.ResumenEgresoSemanalDTO;
import com.talleres.ovycar.dto.HistorialEgresosSemanasDTO;
import com.talleres.ovycar.entity.Egreso;
import com.talleres.ovycar.repository.EgresoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EgresosService {

    @Autowired
    private EgresoRepository egresoRepository;

    public HistorialEgresosSemanasDTO getHistorialSemanas() {
        // Obtener todos los egresos
        List<Egreso> egresos = egresoRepository.findAll();
        
        // Filtrar egresos válidos (con fecha de egreso y monto)
        List<Egreso> egresosValidos = egresos.stream()
                .filter(e -> e.getFechaEgreso() != null && e.getMonto() != null)
                .collect(Collectors.toList());

        if (egresosValidos.isEmpty()) {
            return new HistorialEgresosSemanasDTO(new ArrayList<>(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        // Debug: Imprimir información de cada egreso
        System.out.println("=== DEBUG: EGRESOS Y SUS SEMANAS ===");
        for (Egreso e : egresosValidos) {
            String semana = getSemanaFromDate(e);
            LocalDate fecha = e.getFechaEgreso().toLocalDate();
            
            // Calcular el rango de la semana para mostrar
            LocalDate[] fechasSemana = getFechasSemana(semana);
            LocalDate inicioSemana = fechasSemana[0];
            LocalDate finSemana = fechasSemana[1];
            
            System.out.println("Egreso ID: " + e.getId() + 
                             ", Fecha Egreso: " + e.getFechaEgreso() + 
                             ", Semana calculada: " + semana +
                             ", Rango semana: " + inicioSemana + " a " + finSemana +
                             ", Día de la semana: " + fecha.getDayOfWeek());
        }
        System.out.println("=====================================");

        // Agrupar por semana usando fecha_egreso
        Map<String, List<Egreso>> egresosPorSemana = egresosValidos.stream()
                .collect(Collectors.groupingBy(this::getSemanaFromDate));

        // Debug: Imprimir agrupación por semana
        System.out.println("=== DEBUG: AGRUPACIÓN POR SEMANA ===");
        for (Map.Entry<String, List<Egreso>> entry : egresosPorSemana.entrySet()) {
            System.out.println("Semana: " + entry.getKey() + ", Cantidad: " + entry.getValue().size());
        }
        System.out.println("=====================================");

        // Crear resúmenes semanales
        List<ResumenEgresoSemanalDTO> resumenesSemanales = new ArrayList<>();
        
        for (Map.Entry<String, List<Egreso>> entry : egresosPorSemana.entrySet()) {
            String semana = entry.getKey();
            List<Egreso> egresosSemana = entry.getValue();
            
            ResumenEgresoSemanalDTO resumen = crearResumenSemanal(semana, egresosSemana);
            resumenesSemanales.add(resumen);
        }

        // Ordenar por semana (más reciente primero)
        resumenesSemanales.sort((a, b) -> b.getSemana().compareTo(a.getSemana()));

        // Calcular totales generales
        BigDecimal totalGeneral = resumenesSemanales.stream()
                .map(ResumenEgresoSemanalDTO::getTotalEgresos)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal promedioSemanal = resumenesSemanales.isEmpty() ? BigDecimal.ZERO : 
                                   totalGeneral.divide(BigDecimal.valueOf(resumenesSemanales.size()), 2, BigDecimal.ROUND_HALF_UP);

        // Calcular crecimiento promedio
        BigDecimal crecimientoPromedio = calcularCrecimientoPromedio(resumenesSemanales);

        return new HistorialEgresosSemanasDTO(resumenesSemanales, totalGeneral, promedioSemanal, crecimientoPromedio);
    }

    public ResumenEgresoSemanalDTO getResumenSemana(String semana) {
        // Obtener egresos de la semana específica
        List<Egreso> egresos = egresoRepository.findAll().stream()
                .filter(e -> e.getFechaEgreso() != null && e.getMonto() != null)
                .filter(e -> getSemanaFromDate(e).equals(semana))
                .collect(Collectors.toList());

        return crearResumenSemanal(semana, egresos);
    }

    public List<EgresoSemanalDTO> getEgresosPorFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener egresos en el rango de fechas
        List<Egreso> egresos = egresoRepository.findAll().stream()
                .filter(e -> e.getFechaEgreso() != null && e.getMonto() != null)
                .filter(e -> {
                    LocalDate fechaEgreso = e.getFechaEgreso().toLocalDate();
                    return !fechaEgreso.isBefore(fechaInicio) && !fechaEgreso.isAfter(fechaFin);
                })
                .collect(Collectors.toList());

        return egresos.stream()
                .map(this::convertirAEgresoSemanalDTO)
                .collect(Collectors.toList());
    }

    private ResumenEgresoSemanalDTO crearResumenSemanal(String semana, List<Egreso> egresos) {
        if (egresos.isEmpty()) {
            return new ResumenEgresoSemanalDTO(semana, null, null, BigDecimal.ZERO, 0, BigDecimal.ZERO, BigDecimal.ZERO, new ArrayList<>());
        }

        // Calcular fechas de la semana
        LocalDate[] fechas = getFechasSemana(semana);
        LocalDate fechaInicio = fechas[0];
        LocalDate fechaFin = fechas[1];

        // Calcular totales
        BigDecimal totalEgresos = egresos.stream()
                .map(e -> e.getMonto() != null ? e.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int cantidadEgresos = egresos.size();
        BigDecimal promedioPorEgreso = cantidadEgresos > 0 ? 
                                      totalEgresos.divide(BigDecimal.valueOf(cantidadEgresos), 2, BigDecimal.ROUND_HALF_UP) : 
                                      BigDecimal.ZERO;

        // Calcular crecimiento vs semana anterior
        BigDecimal crecimientoVsSemanaAnterior = calcularCrecimientoVsSemanaAnterior(semana, totalEgresos);

        // Convertir egresos a DTOs
        List<EgresoSemanalDTO> egresosDTO = egresos.stream()
                .map(this::convertirAEgresoSemanalDTO)
                .collect(Collectors.toList());

        return new ResumenEgresoSemanalDTO(semana, fechaInicio, fechaFin, totalEgresos, 
                                         cantidadEgresos, promedioPorEgreso, 
                                         crecimientoVsSemanaAnterior, egresosDTO);
    }

    private EgresoSemanalDTO convertirAEgresoSemanalDTO(Egreso egreso) {
        String categoria = egreso.getCategoria() != null ? egreso.getCategoria() : "Sin categoría";
        String responsable = egreso.getResponsable() != null ? egreso.getResponsable() : "No especificado";
        String semana = egreso.getFechaEgreso() != null ? getSemanaFromDate(egreso) : "";

        return new EgresoSemanalDTO(
            egreso.getId(),
            egreso.getFechaEgreso(),
            egreso.getConcepto(),
            egreso.getMonto(),
            categoria,
            responsable,
            semana
        );
    }

    private String getSemanaFromDate(Egreso egreso) {
        if (egreso.getFechaEgreso() == null) return "";
        
        LocalDate fecha = egreso.getFechaEgreso().toLocalDate();
        
        // Calcular la semana usando el calendario colombiano (domingo a domingo)
        // Encontrar el domingo más cercano hacia atrás
        LocalDate domingoSemana = fecha;
        while (domingoSemana.getDayOfWeek().getValue() != 7) { // 7 = domingo
            domingoSemana = domingoSemana.minusDays(1);
        }
        
        // Si el domingo encontrado es del año anterior, ajustar
        int year = domingoSemana.getYear();
        if (domingoSemana.getYear() < fecha.getYear()) {
            year = fecha.getYear();
        }
        
        // Calcular el número de semana
        LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);
        LocalDate firstSunday = firstDayOfYear;
        while (firstSunday.getDayOfWeek().getValue() != 7) {
            firstSunday = firstSunday.plusDays(1);
        }
        
        // Si la fecha es anterior al primer domingo del año, usar el año anterior
        if (fecha.isBefore(firstSunday)) {
            year = year - 1;
            firstDayOfYear = LocalDate.of(year, 1, 1);
            firstSunday = firstDayOfYear;
            while (firstSunday.getDayOfWeek().getValue() != 7) {
                firstSunday = firstSunday.plusDays(1);
            }
        }
        
        // Calcular la diferencia en días desde el primer domingo
        long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(firstSunday, domingoSemana);
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
            
            // Calcular el domingo de inicio de la semana especificada
            LocalDate startOfWeek = firstSunday.plusWeeks(week - 1);
            // El fin de la semana es el domingo siguiente (para mostrar domingo a domingo)
            LocalDate endOfWeek = startOfWeek.plusDays(7);
            
            return new LocalDate[]{startOfWeek, endOfWeek};
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
            
            LocalDate startOfWeek = firstSunday.plusWeeks(currentWeek - 1);
            LocalDate endOfWeek = startOfWeek.plusDays(7);
            
            return new LocalDate[]{startOfWeek, endOfWeek};
        }
    }

    private BigDecimal calcularCrecimientoVsSemanaAnterior(String semanaActual, BigDecimal totalActual) {
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
            ResumenEgresoSemanalDTO resumenAnterior = getResumenSemana(semanaAnterior);
            BigDecimal totalAnterior = resumenAnterior.getTotalEgresos();
            
            if (totalAnterior.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
            
            return totalActual.subtract(totalAnterior)
                    .divide(totalAnterior, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal calcularCrecimientoPromedio(List<ResumenEgresoSemanalDTO> resumenes) {
        if (resumenes.size() < 2) return BigDecimal.ZERO;
        
        BigDecimal sumaCrecimientos = resumenes.stream()
                .map(ResumenEgresoSemanalDTO::getCrecimientoVsSemanaAnterior)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return sumaCrecimientos.divide(BigDecimal.valueOf(resumenes.size() - 1), 2, BigDecimal.ROUND_HALF_UP);
    }
}
