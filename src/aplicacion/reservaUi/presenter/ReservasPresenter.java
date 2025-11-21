package aplicacion.reservaUi.presenter;

import dto.reserva.ReservaListadoDTO;
import servicios.ReservaService;
import servicios.HuespedService;
import servicios.HabitacionService;
import servicios.FacturaService;

import aplicacion.reservaUi.PanelReservas;
import aplicacion.reservaUi.DialogCrearReserva;
import aplicacion.reservaUi.DialogDetalleReserva;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReservasPresenter {

    // ============================================================
    // Services
    // ============================================================
    private final ReservaService reservaService;
    private final HuespedService huespedService;
    private final HabitacionService habitacionService;
    private final FacturaService facturaService;

    // ============================================================
    // View
    // ============================================================
    private final PanelReservas view;

    // ============================================================
    // Constructor completo (CORRECTO)
    // ============================================================
    public ReservasPresenter(
            PanelReservas view,
            ReservaService reservaService,
            HuespedService huespedService,
            HabitacionService habitacionService,
            FacturaService facturaService
    ) {
        this.view = view;
        this.reservaService = reservaService;
        this.huespedService = huespedService;
        this.habitacionService = habitacionService;
        this.facturaService = facturaService;

        view.setPresenter(this);
    }

    // ============================================================
    // Listado inicial
    // ============================================================
    public void cargarListado() {
        try {
            List<ReservaListadoDTO> lista = reservaService.listarTodas().stream()
                    .map(this::toListadoDTO)
                    .collect(Collectors.toList());

            view.mostrarReservas(lista);

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // ============================================================
    // Buscar por texto libre
    // ============================================================
    public void aplicarBusquedaTexto(String texto) {
        try {
            String t = texto.toLowerCase();

            List<ReservaListadoDTO> lista = reservaService.listarTodas().stream()
                    .map(this::toListadoDTO)
                    .filter(r ->
                            String.valueOf(r.idReserva()).contains(t) ||
                                    r.nombreCompletoHuesped().toLowerCase().contains(t) ||
                                    r.nombreHabitacion().toLowerCase().contains(t)
                    )
                    .collect(Collectors.toList());

            view.mostrarReservas(lista);

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // ============================================================
    // Aplicar filtros (por fecha + estado)
    // ============================================================
    public void aplicarFiltros(LocalDate desde, LocalDate hasta, String estado) {
        try {
            List<ReservaListadoDTO> lista = reservaService.listarTodas().stream()
                    .map(this::toListadoDTO)
                    .filter(r -> !r.desde().isAfter(hasta))
                    .filter(r -> !r.hasta().isBefore(desde))
                    .filter(r -> estado.equals("Todos") || r.estado().equalsIgnoreCase(estado))
                    .collect(Collectors.toList());

            view.mostrarReservas(lista);

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // ============================================================
    // Abrir detalle
    // ============================================================
    public void abrirDetalle(int idReserva) {
        try {
            Window owner = SwingUtilities.getWindowAncestor(view);

            DialogDetalleReserva dialog = new DialogDetalleReserva(owner, idReserva);

            DetalleReservaPresenter p = new DetalleReservaPresenter(
                    reservaService,
                    dialog,
                    facturaService,
                    idReserva
            );

            dialog.setPresenter(p);
            p.cargarDatos();

            dialog.setLocationRelativeTo(view);
            dialog.setVisible(true);

            cargarListado();

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // ============================================================
    // Abrir formulario Crear Reserva
    // ============================================================
    public void abrirCrearReserva() {
        try {
            DialogCrearReserva dialog = new DialogCrearReserva(
                    SwingUtilities.getWindowAncestor(view)
            );

            CrearReservaPresenter p = new CrearReservaPresenter(
                    dialog,
                    reservaService,
                    huespedService,
                    habitacionService,
                    facturaService
            );

            dialog.setPresenter(p);
            dialog.setLocationRelativeTo(view);
            dialog.setVisible(true);

            cargarListado();

        } catch (Exception ex) {
            view.mostrarError(ex.getMessage());
        }
    }

    // ============================================================
    // Conversión dominio → DTO
    // ============================================================
    private ReservaListadoDTO toListadoDTO(dominio.Reserva r) {
        return new ReservaListadoDTO(
                r.getId(),
                r.getHuesped().getId(),
                r.getHuesped().getNombreCompleto(),
                r.getHabitacion().getId(),
                r.getHabitacion().getNombre(),
                r.getDesde(),
                r.getHasta(),
                r.getEstado().name()
        );
    }
}