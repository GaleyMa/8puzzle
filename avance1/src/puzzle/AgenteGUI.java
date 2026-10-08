package puzzle;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.swing.SwingUtilities;

/**
 * Agente GUI (Avance 1).
 * - Al presionar "Iniciar" envía un INFORM "competencia iniciada" a los 3 jugadores.
 * - Recibe las respuestas (AGREE / REFUSE / NOT_UNDERSTOOD) y las muestra en la ventana.
 */
public class AgenteGUI extends Agent {

    private VentanaGUI ventana;
    private int ronda = 0;

    @Override
    protected void setup() {
        SwingUtilities.invokeLater(() -> {
            ventana = new VentanaGUI(this);
            ventana.setVisible(true);
        });

        // Escucha las respuestas de los jugadores de la conversación "competencia"
        MessageTemplate plantilla = MessageTemplate.MatchConversationId(Protocolo.CONVERSACION);

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive(plantilla);
                if (msg == null) {
                    block();
                    return;
                }
                String jugador = msg.getSender().getLocalName();
                String performativa = ACLMessage.getPerformative(msg.getPerformative());
                String texto = msg.getContent();
                SwingUtilities.invokeLater(() ->
                        ventana.mostrarRespuesta(jugador, performativa, texto));
            }
        });
    }

    /**
     * Lo llama la ventana (hilo de Swing). No se envía desde ahí directamente:
     * se encola un OneShotBehaviour para que el envío ocurra en el hilo del agente.
     */
    public void iniciarCompetencia() {
        addBehaviour(new OneShotBehaviour(this) {
            @Override
            public void action() {
                ronda++;
                ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
                for (String nombre : Protocolo.JUGADORES) {
                    inform.addReceiver(new AID(nombre, AID.ISLOCALNAME));
                }
                inform.setOntology(Protocolo.ONTOLOGIA);
                inform.setConversationId(Protocolo.CONVERSACION);
                inform.setReplyWith("ronda-" + ronda);
                inform.setContent(Protocolo.COMPETENCIA_INICIADA);
                send(inform);

                int r = ronda;
                SwingUtilities.invokeLater(() -> ventana.mostrarEnvio(r));
            }
        });
    }

    @Override
    protected void takeDown() {
        if (ventana != null) {
            SwingUtilities.invokeLater(() -> ventana.dispose());
        }
    }
}
