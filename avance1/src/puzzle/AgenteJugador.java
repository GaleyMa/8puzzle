package puzzle;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;

/**
 * Agente Jugador (Avance 1).
 * Espera el INFORM "competencia iniciada" del Agente GUI y responde al azar
 * con una de tres respuestas, cada una con su performativa FIPA:
 *   AGREE          -> "estoy listo"
 *   REFUSE         -> "no quiero jugar"
 *   NOT_UNDERSTOOD -> "no sé las reglas del juego"
 * El mismo código sirve para jugador1, jugador2 y jugador3.
 */
public class AgenteJugador extends Agent {

    private static final int[] PERFORMATIVAS = {
            ACLMessage.AGREE, ACLMessage.REFUSE, ACLMessage.NOT_UNDERSTOOD
    };
    private static final String[] TEXTOS = {
            "estoy listo", "no quiero jugar", "no sé las reglas del juego"
    };

    private final Random random = new Random();

    @Override
    protected void setup() {
        System.out.println("[" + getLocalName() + "] listo y esperando mensajes.");

        // Solo atiende INFORM con la ontología del juego
        MessageTemplate plantilla = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchOntology(Protocolo.ONTOLOGIA));

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive(plantilla);
                if (msg == null) {
                    block();   // duerme hasta que llegue otro mensaje
                    return;
                }
                if (!Protocolo.COMPETENCIA_INICIADA.equals(msg.getContent())) {
                    return;
                }

                int i = random.nextInt(TEXTOS.length);
                ACLMessage respuesta = msg.createReply();  // responde solo a la GUI
                respuesta.setPerformative(PERFORMATIVAS[i]);
                respuesta.setContent(TEXTOS[i]);
                myAgent.send(respuesta);

                System.out.println("[" + getLocalName() + "] -> "
                        + msg.getSender().getLocalName() + ": "
                        + ACLMessage.getPerformative(PERFORMATIVAS[i]) + " \"" + TEXTOS[i] + "\"");
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("[" + getLocalName() + "] terminado.");
    }
}
