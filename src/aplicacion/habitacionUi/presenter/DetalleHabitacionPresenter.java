package aplicacion.habitacionUi.presenter;

import aplicacion.habitacionUi.DialogDetalleHabitacion;
import dto.HabitacionDetalleDTO;
import servicios.HabitacionService;
import validaciones.BusinessRuleException;
import validaciones.InputException;

public class DetalleHabitacionPresenter {

    private final HabitacionService service;
    private final DialogDetalleHabitacion view;
    private final int idHabitacion;

    public DetalleHabitacionPresenter(HabitacionService service,
                                      DialogDetalleHabitacion view,
                                      int idHabitacion) {
        this.service = service;
        this.view = view;
        this.idHabitacion = idHabitacion;
        this.view.setPresenter(this);
    }

    // ============================================================
    // Cargar datos iniciales
    // ============================================================
    public void cargarDatos() {
        try {
            HabitacionDetalleDTO dto = service.obtenerDetalle(idHabitacion);
            view.setDatos(dto);

        } catch (InputException | BusinessRuleException ex) {
            view.showError(ex.getMessage());
            view.cerrar();
        }
    }

    // ============================================================
    // Dar de baja
    // ============================================================
    public void darDeBaja() {
        try {
            HabitacionDetalleDTO dto = service.darDeBaja(idHabitacion);
            view.setDatos(dto);
            view.showSuccess("Habitación dada de baja.");

        } catch (InputException | BusinessRuleException ex) {
            view.showError(ex.getMessage());
        }
    }

    // ============================================================
    // Activar
    // ============================================================
    public void activar() {
        try {
            HabitacionDetalleDTO dto = service.activar(idHabitacion);
            view.setDatos(dto);
            view.showSuccess("Habitación activada.");

        } catch (InputException | BusinessRuleException ex) {
            view.showError(ex.getMessage());
        }
    }
}