package aplicacion.facturaUi.presenter;

import aplicacion.facturaUi.PanelFacturas;
import dto.FacturaListadoDTO;
import servicios.FacturaService;

import java.util.List;

public class FacturasPresenter {

    private final FacturaService facturaService;
    private final PanelFacturas view;

    public FacturaService getFacturaService() {
        return facturaService;
    }

    public FacturasPresenter(PanelFacturas view, FacturaService facturaService) {
        this.view = view;
        this.facturaService = facturaService;
    }

    // ============================================================
    // Cargar facturas iniciales
    // ============================================================
    public void cargarFacturas() {
        List<FacturaListadoDTO> facturas = facturaService.listarFacturas();
        view.mostrarFacturas(facturas);
    }

    // ============================================================
    // Búsqueda
    // ============================================================
    public void buscar(String filtro) {

        if (filtro == null || filtro.isBlank()) {
            limpiar();
            return;
        }

        List<FacturaListadoDTO> resultados = facturaService.buscarFacturas(filtro);
        view.mostrarFacturas(resultados);
    }

    // ============================================================
    // Limpiar
    // ============================================================
    public void limpiar() {
        cargarFacturas();
        view.limpiarFiltro();
    }

    // ============================================================
    // Abrir detalle
    // ============================================================
    public void abrirDetalleFactura(int idFactura) {
        view.abrirDialogoDetalleFactura(idFactura);
    }
}

