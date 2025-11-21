package aplicacion.huespedUi.presenter;

import servicios.HuespedService;
import dto.CrearHuespedDTO;

import aplicacion.huespedUi.DialogEditHuesped;

public class EditHuespedPresenter {

    private final HuespedService service;
    private final DialogEditHuesped view;
    private final int id;

    public EditHuespedPresenter(HuespedService service, DialogEditHuesped view, int id) {
        this.service = service;
        this.view = view;
        this.id = id;
        this.view.setPresenter(this); // MVP
    }

    // ============================================================
    // Guardar cambios
    // ============================================================
    public void onGuardar() {
        try {
            // DNI no se modifica, pero la vista igual lo puede mostrar
            CrearHuespedDTO dto = new CrearHuespedDTO(
                    view.getDni(),        // se ignora si preferís dejarlo igual
                    view.getNombre(),
                    view.getApellido(),
                    view.getEmail()
            );

            service.modificarHuesped(id, dto);

            view.mostrarExito("Datos actualizados correctamente.");
            view.cerrar();

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }
}
