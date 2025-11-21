package aplicacion.dashboardUi;

import aplicacion.facturaUi.PanelFacturas;
import aplicacion.facturaUi.presenter.FacturasPresenter;
import aplicacion.habitacionUi.PanelHabitaciones;
import aplicacion.habitacionUi.presenter.HabitacionesPresenter;
import aplicacion.huespedUi.PanelHuespedes;
import aplicacion.huespedUi.presenter.HuespedesPresenter;
import aplicacion.reservaUi.PanelReservas;
import aplicacion.reservaUi.presenter.ReservasPresenter;
import dto.FacturaListadoDTO;
import dto.reserva.ReservaDashboardDTO;
import servicios.FacturaService;
import servicios.HabitacionService;
import servicios.HuespedService;
import servicios.ReservaService;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PanelControlPresenter {

    private final PanelControl view;

    private final ReservaService reservaService;
    private final FacturaService facturaService;
    private final HabitacionService habitacionService;
    private final HuespedService huespedService;

    public PanelControlPresenter(
            PanelControl view,
            ReservaService reservaService,
            FacturaService facturaService,
            HabitacionService habitacionService,
            HuespedService huespedService
    ) {
        this.view = view;
        this.reservaService = reservaService;
        this.facturaService = facturaService;
        this.habitacionService = habitacionService;
        this.huespedService = huespedService;
    }

    // ============================================================
    // CARGA COMPLETA DEL DASHBOARD
    // ============================================================
    public void cargarDashboard() {
        cargarOcupacion();
        cargarEntradas();
        cargarSalidas();
        cargarPendientes();
        cargarVencidas();
    }

    public void abrirReservas() {
        PanelReservas panel = new PanelReservas();
        ReservasPresenter p =
                new ReservasPresenter(panel, reservaService, huespedService, habitacionService, facturaService);
        panel.setPresenter(p);
        p.cargarListado();
        view.abrirVentana(panel, "Reservas");
    }

    public void abrirHabitaciones() {
        PanelHabitaciones panel = new PanelHabitaciones();
        HabitacionesPresenter p = new HabitacionesPresenter(habitacionService, panel);
        panel.setPresenter(p);
        p.cargarListado();
        view.abrirVentana(panel, "Habitaciones");
    }

    public void abrirHuespedes() {
        PanelHuespedes panel = new PanelHuespedes();
        HuespedesPresenter p = new HuespedesPresenter(huespedService, panel);
        panel.setPresenter(p);
        p.cargarListado();
        view.abrirVentana(panel, "Huéspedes");
    }

    public void abrirFacturas() {
        PanelFacturas panel = new PanelFacturas();
        FacturasPresenter p = new FacturasPresenter(panel, facturaService, reservaService);
        panel.setPresenter(p);
        p.cargarFacturas();
        view.abrirVentana(panel, "Facturas");
    }

    // ============================================================
    // 1) OCUPACIÓN
    // ============================================================
    private void cargarOcupacion() {
        int ocupadas = reservaService.contarHabitacionesOcupadasHoy();
        int total = reservaService.contarHabitacionesTotales();

        int porcentaje = (total == 0) ? 0 : (ocupadas * 100 / total);

        view.getLblOcupacion().setText(porcentaje + " %");
    }

    // ============================================================
    // 2) PRÓXIMAS ENTRADAS
    // ============================================================
    private void cargarEntradas() {

        List<ReservaDashboardDTO> lista =
                reservaService.listarEntradasUI();

        DefaultTableModel model =
                (DefaultTableModel) view.getTablaEntradas().getModel();
        model.setRowCount(0);

        for (ReservaDashboardDTO dto : lista) {
            model.addRow(new Object[]{
                    dto.fecha(),
                    dto.idReserva(),
                    dto.nombreCompleto(),
                    dto.idHabitacion()
            });
        }
    }

    // ============================================================
    // 3) PRÓXIMAS SALIDAS
    // ============================================================
    private void cargarSalidas() {

        List<ReservaDashboardDTO> lista =
                reservaService.listarSalidasUI();

        DefaultTableModel model =
                (DefaultTableModel) view.getTablaSalidas().getModel();
        model.setRowCount(0);

        for (ReservaDashboardDTO dto : lista) {
            model.addRow(new Object[]{
                    dto.fecha(),
                    dto.idReserva(),
                    dto.nombreCompleto(),
                    dto.idHabitacion()
            });
        }
    }

    // ============================================================
    // 4) PENDIENTES DE COBRO
    // ============================================================
    private void cargarPendientes() {

        List<ReservaDashboardDTO> lista =
                reservaService.listarPendientesDeCobroUI();

        DefaultTableModel model =
                (DefaultTableModel) view.getTablaPendientes().getModel();
        model.setRowCount(0);

        for (ReservaDashboardDTO dto : lista) {
            model.addRow(new Object[]{
                    dto.idReserva(),
                    dto.nombreCompleto(),
                    dto.idFactura(),
                    dto.monto()
            });
        }
    }

    // ============================================================
    // 5) FACTURAS VENCIDAS
    // ============================================================
    private void cargarVencidas() {

        List<FacturaListadoDTO> lista =
                facturaService.buscarFacturasVencidasUI();

        DefaultTableModel model =
                (DefaultTableModel) view.getTablaVencidas().getModel();
        model.setRowCount(0);

        for (FacturaListadoDTO dto : lista) {
            model.addRow(new Object[]{
                    dto.idReserva(),
                    dto.nombreCompleto(),
                    dto.idFactura(),
                    dto.monto()// o dto.monto(), según tu DTO
            });
        }
    }
}