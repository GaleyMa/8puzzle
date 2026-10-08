package puzzle;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

/
public class Main {
    public static void main(String[] args) throws Exception {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.GUI, "true");   // consola RMA de JADE
        ContainerController main = rt.createMainContainer(p);

        for (String nombre : Protocolo.JUGADORES) {
            AgentController jugador = main.createNewAgent(nombre, AgenteJugador.class.getName(), null);
            jugador.start();
        }
        AgentController gui = main.createNewAgent("gui", AgenteGUI.class.getName(), null);
        gui.start();
    }
}
