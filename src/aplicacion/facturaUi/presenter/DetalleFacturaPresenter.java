package aplicacion.facturaUi.presenter;

import aplicacion.facturaUi.DialogDetalleFactura;
import dto.FacturaDetalleDTO;
import servicios.FacturaService;
import servicios.ReservaService;


public class DetalleFacturaPresenter {

    private final FacturaService facturaService;
    private final ReservaService reservaService;
    private final DialogDetalleFactura view;
    private final Runnable onClose;

    public DetalleFacturaPresenter(DialogDetalleFactura view,
                                   FacturaService facturaService,
                                   ReservaService reservaService,
                                   Runnable onClose) {
        this.view = view;
        this.facturaService = facturaService;
        this.reservaService = reservaService;
        this.onClose = onClose;
    }

    public void cargarFactura(int idFactura) {
        try {
            FacturaDetalleDTO dto = facturaService.obtenerDetalleFactura(idFactura);
            view.mostrarDetalle(dto);
        } catch (Exception ex) {
            view.mostrarMensaje("Error al cargar factura: " + ex.getMessage());
        }
    }

    public void registrarPago(int idFactura) {
        try {
            facturaService.registrarPago(idFactura);
            reservaService.confirmarReservasConFactura(idFactura);

            view.mostrarMensaje("Pago registrado correctamente.");
            view.dispose();

            if (onClose != null) onClose.run(); // REFRESCO AUTOMÁTICO

        } catch (Exception ex) {
            view.mostrarMensaje("Error al registrar pago: " + ex.getMessage());
        }
    }
}