package aplicacion.reservaUi.presenter;

import aplicacion.huespedUi.DialogCrearHuesped;
import aplicacion.reservaUi.DialogBuscarHuesped;
import aplicacion.reservaUi.DialogCrearReserva;
import aplicacion.reservaUi.DialogSeleccionarHabitacion;
import aplicacion.huespedUi.presenter.CrearHuespedPresenter;

import dto.reserva.CrearReservaDTO;
import dto.HuespedListadoDTO;
import dto.HabitacionListadoDTO;
import dto.HuespedDetalleDTO;

import servicios.HuespedService;
import servicios.HabitacionService;
import servicios.FacturaService;
import servicios.ReservaService;

import validaciones.InputException;
import validaciones.BusinessRuleException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class CrearReservaPresenter {

    private final DialogCrearReserva view;
    private final HuespedService huespedService;
    private final HabitacionService habitacionService;
    private final FacturaService facturaService;
    private final ReservaService reservaService;

    public CrearReservaPresenter(
            DialogCrearReserva view,
            ReservaService reservaService,
            HuespedService huespedService,
            HabitacionService habitacionService,
            FacturaService facturaService
    ) {
        this.view = view;
        this.huespedService = huespedService;
        this.habitacionService = habitacionService;
        this.facturaService = facturaService;
        this.reservaService = reservaService;

        this.view.setPresenter(this);
    }


    // ================================================================
    // BUSCAR HUESPED
    // ================================================================
    public void onBuscarHuesped() {

        String id = view.getIdHuesped();
        String dni = view.getDni();
        String nombre = view.getNombre();
        String apellido = view.getApellido();

        List<HuespedListadoDTO> lista =
                huespedService.buscar(id, dni, nombre, apellido);

        if (lista.isEmpty()) {
            view.mostrarMensaje("No se encontraron huéspedes.");
            return;
        }

        DialogBuscarHuesped dialog =
                new DialogBuscarHuesped(view.getOwnerWindow(), lista);

        dialog.setVisible(true);

        HuespedListadoDTO sel = dialog.getSeleccionado();
        if (sel != null)
            view.completarHuesped(sel);
    }


    // ================================================================
    // CREAR HUESPED
    // ================================================================
    public void onCrearHuesped() {

        DialogCrearHuesped dialog =
                new DialogCrearHuesped(view);

        // Conectar presenter EXACTAMENTE igual que en PanelHuesped
        CrearHuespedPresenter hp =
                new CrearHuespedPresenter(huespedService, dialog);
        dialog.setPresenter(hp);

        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);

        HuespedDetalleDTO nuevo = dialog.getCreado();

        if (nuevo != null) {
            view.completarHuesped(
                    new HuespedListadoDTO(
                            nuevo.id(),
                            nuevo.dni(),
                            nuevo.nombre(),
                            nuevo.apellido(),
                            nuevo.email(),
                            nuevo.estado()
                    )
            );
        }
    }


    // ================================================================
    // SELECCIONAR HABITACION
    // ================================================================
    public void onSeleccionarHabitacion() {

        LocalDate desde = toLocalDate(view.getFechaDesde());
        LocalDate hasta = toLocalDate(view.getFechaHasta());

        try {
            List<HabitacionListadoDTO> disponibles =
                    habitacionService.buscarDisponibles(desde, hasta);

            if (disponibles.isEmpty()) {
                view.mostrarMensaje("No hay habitaciones disponibles.");
                return;
            }

            DialogSeleccionarHabitacion dialog =
                    new DialogSeleccionarHabitacion(
                            view.getOwnerWindow(),
                            desde,
                            hasta,
                            disponibles
                    );

            dialog.setVisible(true);

            HabitacionListadoDTO sel = dialog.getSeleccionada();
            if (sel != null)
                view.setHabitacion(sel.id());

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }


    // ================================================================
    // CONFIRMAR RESERVA
    // ================================================================
    public void onConfirmarReserva() {

        try {
            int idHuesped = Integer.parseInt(view.getIdHuesped());
            int idHabitacion = Integer.parseInt(view.getHabitacionId());

            LocalDate desde = toLocalDate(view.getFechaDesde());
            LocalDate hasta = toLocalDate(view.getFechaHasta());

            String metodoUI = view.getMetodoPago();  // TRANSFERENCIA o TARJETA

            String metodoEnum = metodoUI.equals("TARJETA")
                    ? "TARJETA"
                    : "TRANSFERENCIA";

            CrearReservaDTO dto = new CrearReservaDTO(
                    idHuesped,
                    idHabitacion,
                    desde,
                    hasta,
                    metodoEnum,
                    view.getCuotas()
            );


            reservaService.crearReservaDesdeUI(
                    dto,
                    huespedService,
                    habitacionService,
                    facturaService
            );

            view.mostrarMensaje("Reserva creada correctamente.");
            view.cerrar();

        } catch (NumberFormatException ex) {
            view.mostrarError("Debe seleccionar huésped y habitación.");
        } catch (InputException | BusinessRuleException ex) {
            view.mostrarError(ex.getMessage());
        } catch (Exception ex) {
            view.mostrarError("Error inesperado: " + ex.getMessage());
        }
    }


    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}