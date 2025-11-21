package aplicacion.habitacionUi.presenter;

import dto.CrearHabitacionDTO;
import servicios.HabitacionService;
import validaciones.InputException;
import validaciones.BusinessRuleException;
import aplicacion.habitacionUi.DialogCrearHabitacion;

public class CrearHabitacionPresenter {

    private final HabitacionService service;
    private final DialogCrearHabitacion view;

    public CrearHabitacionPresenter(HabitacionService service, DialogCrearHabitacion view) {
        this.service = service;
        this.view = view;
        this.view.setPresenter(this);
    }

    // ============================================================
    // Evento: crear habitación
    // ============================================================
    public void crearHabitacion() {
        try {
            CrearHabitacionDTO dto = new CrearHabitacionDTO(
                    view.getNombre(),
                    view.getDescripcion(),
                    view.getPrecioDouble(),
                    view.getTipoHabitacion(),
                    view.getCapacidad()
            );

            int nuevoId = service.crearHabitacion(dto);

            view.showSuccess("Habitación creada correctamente (ID " + nuevoId + ")");
            view.cerrar();

        } catch (InputException | BusinessRuleException ex) {
            view.showError(ex.getMessage());
        } catch (Exception ex) {
            view.showError("Error inesperado: " + ex.getMessage());
        }
    }

}
