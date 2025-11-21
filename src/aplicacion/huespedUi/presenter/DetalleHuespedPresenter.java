package aplicacion.huespedUi.presenter;

import servicios.HuespedService;
import dto.HuespedDetalleDTO;

import aplicacion.huespedUi.DialogDetalleHuesped;
import aplicacion.huespedUi.DialogEditHuesped;

import java.awt.*;

public class DetalleHuespedPresenter {

    private final HuespedService service;
    private final DialogDetalleHuesped view;
    private final int id;

    public DetalleHuespedPresenter(HuespedService service, DialogDetalleHuesped view, int id) {
        this.service = service;
        this.view = view;
        this.id = id;
        this.view.setPresenter(this); // Conexión MVP
    }

    // ============================================================
    // Inicial
    // ============================================================
    public void cargarDatos() {
        try {
            HuespedDetalleDTO dto = service.obtenerDetalle(id);
            view.mostrarDatos(dto);
        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // ============================================================
    // Dar de baja
    // ============================================================
    public void onDarDeBaja() {
        try {
            HuespedDetalleDTO dto = service.darDeBaja(id);

            view.mostrarMensaje("El huésped fue dado de baja con éxito.");
            view.cerrar(); // <-- como pediste
        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // ============================================================
    // Reactivar
    // ============================================================
    public void onReactivar() {
        try {
            HuespedDetalleDTO dto = service.activar(id);

            view.mostrarMensaje("El huésped ha sido reactivado.");
            view.cerrar();
        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // ============================================================
    // Editar
    // ============================================================
    public void onEditar() {
        try {
            Window owner = view.getOwnerWindow();

            DialogEditHuesped dialog = new DialogEditHuesped(
                    owner,
                    id,
                    view.getDni(),
                    view.getNombre(),
                    view.getApellido(),
                    view.getEmail()
            );

            EditHuespedPresenter p = new EditHuespedPresenter(service, dialog, id);
            dialog.setPresenter(p);

            dialog.setVisible(true);

            // Al cerrar el editor, refrescamos el detalle
            cargarDatos();

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }
}