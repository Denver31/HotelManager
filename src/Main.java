
import aplicacion.Sistema;
import aplicacion.dashboardUi.PanelControl;
import aplicacion.dashboardUi.PanelControlPresenter;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        Sistema sistema = new Sistema();

        SwingUtilities.invokeLater(() -> {

            PanelControl view = new PanelControl(sistema);

            PanelControlPresenter presenter = new PanelControlPresenter(
                    view,
                    sistema.getReservaService(),
                    sistema.getFacturaService(),
                    sistema.getHabitacionService(),
                    sistema.getHuespedService()
            );

            view.setPresenter(presenter);

            JFrame frame = new JFrame("Hotel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(view);
            frame.setVisible(true);
        });
    }
}
