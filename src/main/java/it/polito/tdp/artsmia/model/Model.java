package it.polito.tdp.artsmia.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {
	
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private List<Adiacenza> adiacenze;
	private ArtsmiaDAO dao;
	
	private List<Integer> best;
	
	public Model() {
		this.dao = new ArtsmiaDAO();
	}
	
	public List<String> getRuoli(){
		return this.dao.getRuoli();
	}
	
	public int vertici() {
		return this.grafo.vertexSet().size();
		
	}
	
	public int archi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Adiacenza> getAdiacenze(){
		return this.adiacenze;
	}
	
	public void creaGrafo(String ruolo) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(this.grafo, this.dao.getArtisti(ruolo));
		
		adiacenze = this.dao.getAdiacenze(ruolo);
		
		
		//Aggiungo insieme vertici e archi facendo attenzione a non inserirli se sono già presenti
		for(Adiacenza a: adiacenze) {
			/*if(!this.grafo.containsVertex(a.getA1())) {
				this.grafo.addVertex(a.getA1());
			}
			if(!this.grafo.containsVertex(a.getA2())) {
				this.grafo.addVertex(a.getA2());
			}*/
			if(this.grafo.getEdge(a.getA1(), a.getA2()) == null) {
				Graphs.addEdge(this.grafo, a.getA1(), a.getA2(), a.getPeso());
			}
		}
		
		System.out.println("Grafo creato!");
		System.out.println("# VERTICI: " + this.grafo.vertexSet().size());
		System.out.println("# ARCHI: " + this.grafo.edgeSet().size());
		
	}
	
	public boolean grafoContiene(Integer id) {
		if(this.grafo.containsVertex(id))
			return true;
		else
			return false;
	}
	
	public List<Integer> trovaPercorso(Integer sorgente){ //Il metodo riceve il nodo di partenza
		this.best = new ArrayList<>();
		List<Integer> parziale = new ArrayList<>();
		parziale.add(sorgente);
		//Lancio la ricorsione
		ricorsione(parziale, -1);
		
		return best;
	}
	
	private void ricorsione(List<Integer> parziale, int peso) {
		
		int ultimo = parziale.get(parziale.size()-1); //Prendo l'ultimo elemento aggiunto a parziale
		//Ottengo la lista di vicini dell'ultimo elemento aggiunto
		List<Integer> vicini = Graphs.neighborListOf(this.grafo, ultimo);
		
		for(Integer vicino: vicini) {
			if(!parziale.contains(vicino) && peso == -1) {
				parziale.add(vicino);
				ricorsione(parziale, (int) this.grafo.getEdgeWeight(this.grafo.getEdge(ultimo, vicino)));
				parziale.remove(vicino);
			}else {
				if(!parziale.contains(vicino) && this.grafo.getEdgeWeight(this.grafo.getEdge(ultimo, vicino)) == peso) {
					parziale.add(vicino);
					ricorsione(parziale, peso);
					parziale.remove(vicino);
				}
			}
		}
		
		if(parziale.size() > best.size()) {
			this.best = new ArrayList<>(parziale);
		}
	}

}
