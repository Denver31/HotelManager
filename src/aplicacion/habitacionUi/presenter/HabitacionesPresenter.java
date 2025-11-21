package aplicacion.habitacionUi.presenter;

import aplicacion.habitacionUi.PanelHabitaciones;
import servicios.HabitacionService;
import validaciones.BusinessRuleException;
import validaciones.InputException;
import java.time.LocalDate;
import java.util.List;
import dto.HabitacionListadoDTO;
import aplicacion.habitacionUi.DialogCrearHabitacion;
import aplicacion.habitacionUi.DialogDetalleHabitacion;
import java.awt.Window;


import javax.swing.*;

public class HabitacionesPresenter {

    private final HabitacionService service;
    private final PanelHabitaciones view;

    public HabitacionesPresenter(HabitacionService service, PanelHabitaciones view) {
        this.service = service;
        this.view = view;
    }

    // ============================================================
    //   CARGA INICIAL
    // ============================================================
    public void cargarListado() {
        try {
            List<HabitacionListadoDTO> lista = service.obtenerListado();
            view.setListado(lista);

        } catch (RuntimeException ex) {
            view.showError(ex.getMessage());
        }
    }

    // ============================================================
    //   BÚSQUEDA POR TEXTO
    // ============================================================
    public void buscarPorTexto() {
        try {
            String filtro = view.getFiltroTexto();

            if (filtro.isEmpty()) {
                cargarListado();
                return;
            }

            List<HabitacionListadoDTO> lista = service.obtenerListado().stream()
                    .filter(h ->
                            h.nombre().toLowerCase().contains(filtro.toLowerCase()) ||
                                    String.valueOf(h.id()).equals(filtro)
                    )
                    .toList();

            view.setListado(lista);

        } catch (RuntimeException ex) {
            view.showError(ex.getMessage());
        }
    }

    // ============================================================
    //   FILTRO POR FECHAS (DISPONIBLES)
    // ============================================================
    public void filtrarPorFechas() {
        try {
            LocalDate desde = view.getFechaDesde();
            LocalDate hasta = view.getFechaHasta();

            List<HabitacionListadoDTO> lista = service.buscarDisponibles(desde, hasta);
            view.setListado(lista);

        } catch (RuntimeException ex) {
            view.showError(ex.getMessage());
        }
    }

    // ============================================================
    //   LIMPIAR PANTALLA
    // ============================================================
    public void limpiar() {
        try {
            view.limpiarFiltros();
            cargarListado();

        } catch (RuntimeException ex) {
            view.showError(ex.getMessage());
        }
    }

    // ============================================================
    //   NUEVA HABITACIÓN
    // ============================================================
    public void nuevaHabitacion() {
        try {
            Window owner = SwingUtilities.getWindowAncestor(view);
            DialogCrearHabitacion dialog = new DialogCrearHabitacion(owner);

            new CrearHabitacionPresenter(service, dialog); // el constructor hace setPresenter()

            dialog.setLocationRelativeTo(view);
            dialog.setVisible(true);

            cargarListado();

        } catch (RuntimeException ex) {
            view.showError(ex.getMessage());
        }
    }

    // ============================================================
    //   ABRIR DETALLE
    // ============================================================
    public void abrirDetalle() {
        try {
            int idSeleccionado = view.getHabitacionSeleccionada();
            if (idSeleccionado == -1) {
                view.showError("Debe seleccionar una habitación.");
                return;
            }

            Window owner = SwingUtilities.getWindowAncestor(view);
            DialogDetalleHabitacion dialog = new DialogDetalleHabitacion(owner, idSeleccionado);

            DetalleHabitacionPresenter detallePresenter =
                    new DetalleHabitacionPresenter(service, dialog, idSeleccionado);

            detallePresenter.cargarDatos();

            dialog.setLocationRelativeTo(view);
            dialog.setVisible(true);

            cargarListado();

        } catch (RuntimeException ex) {
            view.showError(ex.getMessage());
        }
    }
}
