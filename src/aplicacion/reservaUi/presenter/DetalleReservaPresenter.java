package aplicacion.reservaUi.presenter;

import dto.reserva.ReservaDetalleDTO;
import dominio.Reserva.EstadoReserva;

import servicios.ReservaService;
import servicios.FacturaService;
import aplicacion.reservaUi.DialogDetalleReserva;

import java.time.LocalDate;

public class DetalleReservaPresenter {

    private final ReservaService service;
    private final DialogDetalleReserva view;
    private final FacturaService facturaService;
    private final int idReserva;

    public DetalleReservaPresenter(ReservaService service, DialogDetalleReserva view, FacturaService facturaService, int idReserva) {
        this.service = service;
        this.view = view;
        this.facturaService = facturaService;
        this.idReserva = idReserva;

        this.view.setPresenter(this);
    }

    // ============================================================
    // Cargar datos iniciales
    // ============================================================
    public void cargarDatos() {
        try {
            ReservaDetalleDTO dto = service.obtenerDetalle(idReserva);
            view.mostrarDatos(dto);

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // ============================================================
    // CHECK-IN
    // ============================================================
    public void onCheckIn() {
        try {
            ReservaDetalleDTO dto = service.obtenerDetalle(idReserva);

            EstadoReserva estado = EstadoReserva.valueOf(dto.estado());

            // Validaciones previas a la operación (UI-level)
            if (!(estado == EstadoReserva.PENDIENTE || estado == EstadoReserva.CONFIRMADA)) {
                view.mostrarError("El check-in solo es posible en PENDIENTE o CONFIRMADA.");
                return;
            }

            if (LocalDate.now().isBefore(dto.desde())) {
                view.mostrarError("No se puede realizar check-in antes de la fecha de ingreso.");
                return;
            }

            // Operación de dominio
            service.hacerCheckIn(idReserva);

            view.mostrarMensaje("Check-in realizado correctamente.");
            view.cerrar();

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // ============================================================
    // CHECK-OUT
    // ============================================================
    public void onCheckOut() {
        try {
            ReservaDetalleDTO dto = service.obtenerDetalle(idReserva);
            EstadoReserva estado = EstadoReserva.valueOf(dto.estado());

            if (estado != EstadoReserva.ACTIVA) {
                view.mostrarError("El check-out solo es posible cuando la reserva está ACTIVA.");
                return;
            }

            service.hacerCheckOut(idReserva);

            view.mostrarMensaje("Check-out realizado correctamente.");
            view.cerrar();

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // ============================================================
    // CANCELAR
    // ============================================================
    public void onCancelar() {
        try {
            ReservaDetalleDTO dto = service.obtenerDetalle(idReserva);

            if (dto.estado().equals("ACTIVA")) {
                view.mostrarError("No se puede cancelar una reserva activa (ya tiene check-in realizado).");
                return;
            }

            // 1) Cancelar reserva
            service.cancelar(idReserva);

            // 2) Cancelar factura asociada (funcional)
            facturaService.cancelarFacturaAsociada(dto.idFactura());

            view.mostrarMensaje("Reserva y factura canceladas correctamente.");
            view.cerrar();

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }
}
