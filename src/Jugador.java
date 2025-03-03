import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;

public class Jugador {
    private int DISTANCIA = 40;
    private int MARGEN = 10;
    private int TOTAL_CARTAS = 10;
    private int MINIMA_CANTIDAD_GRUPO = 2;
    private Carta[] cartas = new Carta[TOTAL_CARTAS];
    private Random r = new Random();

    public void repartir() {
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            cartas[i] = new Carta(r);
        }
    }

    public void mostrar(JPanel pnl) {
        pnl.removeAll();
        int x = MARGEN + (TOTAL_CARTAS - 1) * DISTANCIA;
        for (Carta carta : cartas) {
            carta.mostrar(pnl, x, MARGEN);
            x -= DISTANCIA;
        }
        pnl.repaint();
    }

    public String getGrupos() {
        StringBuilder mensaje = new StringBuilder();
        int[] contadores = new int[NombreCarta.values().length];
        for (Carta carta : cartas) {
            contadores[carta.getNombre().ordinal()]++;
        }

        for (int i = 0; i < contadores.length; i++) {
            if (contadores[i] >= MINIMA_CANTIDAD_GRUPO) {
                mensaje.append(contadores[i] == 2 ? "PAR" : "TERNA").append(" de ")
                        .append(NombreCarta.values()[i]).append("\n");
            }
        }
        
        String escaleras = getEscaleras();
        if (!escaleras.isEmpty()) {
            mensaje.append(escaleras);
        }

        mensaje.append("Puntaje de cartas que no están en grupos: ").append(calcularPuntaje());
        return mensaje.toString();
    }

    private String getEscaleras() {
        ArrayList<String> escalerasEncontradas = new ArrayList<>();
        for (Pinta pinta : Pinta.values()) {
            ArrayList<Carta> cartasMismoPalo = new ArrayList<>();
            for (Carta carta : cartas) {
                if (carta.getPinta() == pinta) {
                    cartasMismoPalo.add(carta);
                }
            }
            cartasMismoPalo.sort((c1, c2) -> c1.getNombre().ordinal() - c2.getNombre().ordinal());

            int contador = 1;
            StringBuilder escaleraActual = new StringBuilder(cartasMismoPalo.isEmpty() ? "" : cartasMismoPalo.get(0).getNombre().toString());
            for (int i = 1; i < cartasMismoPalo.size(); i++) {
                if (cartasMismoPalo.get(i).getNombre().ordinal() == cartasMismoPalo.get(i - 1).getNombre().ordinal() + 1) {
                    contador++;
                    escaleraActual.append(", ").append(cartasMismoPalo.get(i).getNombre().toString());
                } else {
                    if (contador >= 3) {
                        escalerasEncontradas.add(contador + " de " + pinta + " (" + escaleraActual + ")");
                    }
                    contador = 1;
                    escaleraActual = new StringBuilder(cartasMismoPalo.get(i).getNombre().toString());
                }
            }
            if (contador >= 3) {
                escalerasEncontradas.add(contador + " de " + pinta + " (" + escaleraActual + ")");
            }
        }
        return escalerasEncontradas.isEmpty() ? "" : "Escaleras encontradas:\n" + String.join("\n", escalerasEncontradas) + "\n";
    }

    private int calcularPuntaje() {
        int puntaje = 0;
        boolean[] enGrupoOEscalera = new boolean[TOTAL_CARTAS];
        
        int[] contadores = new int[NombreCarta.values().length];
        for (Carta carta : cartas) {
            contadores[carta.getNombre().ordinal()]++;
        }
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            if (contadores[cartas[i].getNombre().ordinal()] >= MINIMA_CANTIDAD_GRUPO) {
                enGrupoOEscalera[i] = true;
            }
        }
        
        for (Pinta pinta : Pinta.values()) {
            ArrayList<Integer> indices = new ArrayList<>();
            for (int i = 0; i < TOTAL_CARTAS; i++) {
                if (cartas[i].getPinta() == pinta) {
                    indices.add(i);
                }
            }
            indices.sort((i1, i2) -> cartas[i1].getNombre().ordinal() - cartas[i2].getNombre().ordinal());

            int contador = 1;
            for (int i = 1; i < indices.size(); i++) {
                if (cartas[indices.get(i)].getNombre().ordinal() == cartas[indices.get(i - 1)].getNombre().ordinal() + 1) {
                    contador++;
                } else {
                    if (contador >= 3) {
                        for (int j = 0; j < contador; j++) {
                            enGrupoOEscalera[indices.get(i - j - 1)] = true;
                        }
                    }
                    contador = 1;
                }
            }
            if (contador >= 3) {
                for (int j = 0; j < contador; j++) {
                    enGrupoOEscalera[indices.get(indices.size() - j - 1)] = true;
                }
            }
        }
        
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            if (!enGrupoOEscalera[i]) {
                NombreCarta nombre = cartas[i].getNombre();
                puntaje += (nombre == NombreCarta.AS || nombre == NombreCarta.JACK || nombre == NombreCarta.QUEEN || nombre == NombreCarta.KING) ? 10 : nombre.ordinal() + 1;
            }
        }
        return puntaje;
    }
}
