package com.talleres.ovycar.service;

import com.talleres.ovycar.entity.Auditoria;
import com.talleres.ovycar.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registro de auditoría solo desde el backend (sin API de consulta; datos en tabla {@code auditorias}).
 */
@Service
@RequiredArgsConstructor
public class AuditoriaService {

    public static final String MOD_CLIENTE = "CLIENTE";
    public static final String MOD_VEHICULO = "VEHICULO";
    public static final String MOD_MANTENIMIENTO = "MANTENIMIENTO";
    public static final String MOD_EGRESO = "EGRESO";

    public static final String ACC_CREAR = "CREAR";
    public static final String ACC_ACTUALIZAR = "ACTUALIZAR";
    public static final String ACC_ELIMINAR = "ELIMINAR";
    public static final String ACC_CAMBIO_ESTADO = "CAMBIO_ESTADO";

    private static final int RESUMEN_MAX = 2048;

    private final AuditoriaRepository auditoriaRepository;

    @Transactional
    public void registrar(String modulo, String accion, Long entidadId, String resumen) {
        String usuario = usuarioActual();
        String texto = truncar(resumen != null ? resumen : "");
        Auditoria row = new Auditoria();
        row.setUsuario(usuario);
        row.setModulo(modulo);
        row.setAccion(accion);
        row.setEntidadId(entidadId);
        row.setResumen(texto);
        auditoriaRepository.save(row);
    }

    private static String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            return "anonimo";
        }
        String name = auth.getName();
        return name.length() > 80 ? name.substring(0, 80) : name;
    }

    private static String truncar(String s) {
        if (s.length() <= RESUMEN_MAX) {
            return s;
        }
        return s.substring(0, RESUMEN_MAX - 1) + "…";
    }
}
