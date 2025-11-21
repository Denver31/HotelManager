package aplicacion.huespedUi.presenter;

import servicios.HuespedService;
import dto.HuespedListadoDTO;

import aplicacion.huespedUi.PanelHuespedes;
import aplicacion.huespedUi.DialogCrearHuesped;
import aplicacion.huespedUi.DialogDetalleHuesped;

import java.awt.*;

public class HuespedesPresenter {

    private final HuespedService service;
    private final PanelHuespedes view;

    public HuespedesPresenter(HuespedService service, PanelHuespedes view) {
        this.service = service;
        this.view = view;
        this.view.setPresenter(this); // CONEXIÓN MVP
    }

    // ============================================================
    // Cargar listado inicial
    // ============================================================
    public void cargarListado() {
        var lista = service.obtenerListado();
        view.mostrarListado(lista);
    }

    // ============================================================
    // Búsqueda texto
    // ============================================================
    public void onBuscar(String filtro) {
        var lista = service.buscarPorTexto(filtro);
        view.mostrarListado(lista);
    }

    // ============================================================
    // Limpiar filtros
    // ============================================================
    public void onLimpiar() {
        view.limpiarFiltro();
        cargarListado();
    }

    // ============================================================
    // Nuevo huésped
    // ============================================================
    public void onNuevo() {
        Window owner = view.getParentWindow();

        DialogCrearHuesped dialog = new DialogCrearHuesped(owner);
        CrearHuespedPresenter p = new CrearHuespedPresenter(service, dialog);

        dialog.setPresenter(p);
        dialog.setVisible(true);

        // Tras crear, recargar tabla
        cargarListado();
    }

    // ============================================================
    // Seleccionar fila (doble click)
    // ============================================================
    public void onSeleccionar(int idHuesped) {
        Window owner = view.getParentWindow();

        DialogDetalleHuesped dialog = new DialogDetalleHuesped(owner, idHuesped);
        DetalleHuespedPresenter p = new DetalleHuespedPresenter(service, dialog, idHuesped);

        dialog.setPresenter(p);
        p.cargarDatos();

        dialog.setVisible(true);

        // tras cerrar, refrescar listado
        cargarListado();
    }
}
