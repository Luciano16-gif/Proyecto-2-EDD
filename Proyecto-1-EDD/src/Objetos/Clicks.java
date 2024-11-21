/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objetos;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
/**
 *
 * @author pseba
 */
public class Clicks implements ViewerListener {
    protected boolean loop = true;
    private ArbolGenealogico arbolGenealogico;

    public Clicks(Graph grafo, ArbolGenealogico arbolGenealogico) {
        this.arbolGenealogico = arbolGenealogico;
        Graph graph = grafo;
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        
        // Activa las opciones de interacción con el mouse
        viewer.getDefaultView().enableMouseOptions();
        
        // Configura el ViewerPipe
        ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this); // Agrega el listener de clic
        fromViewer.addSink(graph);

        // Bucle de eventos en hilo separado
        new Thread(() -> {
            while (loop) {
                fromViewer.pump();  // Usa pump() en lugar de blockingPump()
                try {
                    Thread.sleep(50);  // Añade un pequeño retraso para reducir carga
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    @Override
    public void viewClosed(String id) {
        loop = false;
    }

    @Override
    public void buttonPushed(String id) {
       arbolGenealogico.mostrarInformacionNodo(id);
    }

    @Override
    public void buttonReleased(String id) {
        //System.out.println("Button released on node " + id);
    }

    @Override
    public void mouseOver(String id) {
        //System.out.println("Mouse over node " + id);
    }

    @Override
    public void mouseLeft(String id) {
        //System.out.println("Mouse left node " + id);
    }
}