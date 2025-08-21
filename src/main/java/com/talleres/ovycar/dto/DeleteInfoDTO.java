package com.talleres.ovycar.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteInfoDTO {
    private boolean canDelete;
    private String reason;
    private int facturasCount;
    private List<FacturaInfoDTO> facturas;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FacturaInfoDTO {
        private Long id;
        private String numeroFactura;
        private String estado;
        private String fechaEmision;
        private Double total;
    }
}
