package aplicacion.huespedUi.presenter;

import servicios.HuespedService;
import dto.CrearHuespedDTO;

import aplicacion.huespedUi.DialogCrearHuesped;

public class CrearHuespedPresenter {

    private final HuespedService service;
    private final DialogCrearHuesped view;

    public CrearHuespedPresenter(HuespedService service, DialogCrearHuesped view) {
        this.service = service;
        this.view = view;
        this.view.setPresenter(this); // MVP
    }

    // ============================================================
    // Crear huésped
    // ============================================================
    public void onCrear() {
        try {
            CrearHuespedDTO dto = new CrearHuespedDTO(
                    view.getDni(),
                    view.getNombre(),
                    view.getApellido(),
                    view.getEmail()
            );

            int id = service.crearHuesped(dto);

            view.mostrarExito("Huésped creado (ID: " + id + ")");
            view.cerrar();

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }
}

