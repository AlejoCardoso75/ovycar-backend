package com.talleres.ovycar.util;

import java.time.LocalDate;

/**
 * Clase utilitaria para el cálculo de semanas siguiendo el calendario colombiano
 * de lunes a domingo. Centraliza la lógica para asegurar consistencia
 * entre todos los módulos del sistema.
 */
public class SemanaUtil {
    
    /**
     * Calcula la semana del año para una fecha dada.
     * Las semanas van de lunes a lunes (calendario colombiano ajustado).
     * 
     * @param fecha La fecha para calcular la semana
     * @return String en formato "YYYY-WW" (año-semana)
     */
    public static String getSemanaFromDate(LocalDate fecha) {
        if (fecha == null) return "";
        
        // Calcular semana de lunes a lunes (calendario colombiano ajustado)
        // Semana 1 comienza el primer lunes del año
        int year = fecha.getYear();
        
        // Encontrar el primer lunes del año
        LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);
        LocalDate firstMonday = firstDayOfYear;
        while (firstMonday.getDayOfWeek().getValue() != 1) { // 1 = lunes
            firstMonday = firstMonday.plusDays(1);
        }
        
        // Calcular la semana
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(firstMonday, fecha);
        int week = (int) (daysBetween / 7) + 1;
        
        // Si la fecha está antes del primer lunes, pertenece a la semana 0 del año anterior
        if (daysBetween < 0) {
            year = year - 1;
            week = 52; // Última semana del año anterior
        }
        
        return String.format("%d-%02d", year, week);
    }
    
    /**
     * Obtiene las fechas de inicio y fin de una semana específica.
     * 
     * @param semana String en formato "YYYY-WW"
     * @return Array con [fechaInicio, fechaFin] donde fechaInicio es lunes y fechaFin es domingo
     */
    public static LocalDate[] getFechasSemana(String semana) {
        try {
            String[] parts = semana.split("-");
            int year = Integer.parseInt(parts[0]);
            int week = Integer.parseInt(parts[1]);
            
            // Calcular fechas de la semana de lunes a domingo (calendario colombiano ajustado)
            // Semana 1 comienza el primer lunes del año
            LocalDate firstDayOfYear = LocalDate.of(year, 1, 1);
            LocalDate firstMonday = firstDayOfYear;
            while (firstMonday.getDayOfWeek().getValue() != 1) { // 1 = lunes
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
            int currentYear = now.getYear();
            
            // Encontrar el primer lunes del año actual
            LocalDate firstDayOfYear = LocalDate.of(currentYear, 1, 1);
            LocalDate firstMonday = firstDayOfYear;
            while (firstMonday.getDayOfWeek().getValue() != 1) {
                firstMonday = firstMonday.plusDays(1);
            }
            
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(firstMonday, now);
            int currentWeek = (int) (daysBetween / 7) + 1;
            
            return getFechasSemana(String.format("%d-%02d", currentYear, currentWeek));
        }
    }
    
    /**
     * Obtiene la semana actual en formato "YYYY-WW"
     * 
     * @return String con la semana actual
     */
    public static String getSemanaActual() {
        return getSemanaFromDate(LocalDate.now());
    }
}
