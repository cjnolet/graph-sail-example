package org.calrissian.graphsail;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.oupls.sail.GraphSail;
import info.aduna.iteration.CloseableIteration;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.impl.EmptyBindingSet;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;

/**
 * Created by cjnolet on 4/12/14.
 */
public class Driver {

    public static void main(String args[]) throws SailException, MalformedQueryException, QueryEvaluationException {
        TinkerGraph graph = new TinkerGraph();
        Sail sail = new GraphSail(graph);
        sail.initialize();
        SailConnection sc = sail.getConnection();
        ValueFactory vf = sail.getValueFactory();
        sc.addStatement(vf.createURI("http://tinkerpop.com#1"), vf.createURI("http://tinkerpop.com#knows"), vf.createURI("http://tinkerpop.com#3"), vf.createURI("http://tinkerpop.com"));
        sc.addStatement(vf.createURI("http://tinkerpop.com#1"), vf.createURI("http://tinkerpop.com#name"), vf.createLiteral("marko"), vf.createURI("http://tinkerpop.com"));
        sc.addStatement(vf.createURI("http://tinkerpop.com#3"), vf.createURI("http://tinkerpop.com#name"), vf.createLiteral("josh"), vf.createURI("http://tinkerpop.com"));

        System.out.println("get statements: ?s ?p ?o ?g");
        CloseableIteration<? extends Statement, SailException> results = sc.getStatements(null, null, null, false);
        while(results.hasNext()) {
            System.out.println(results.next());
        }

        System.out.println("\nget statements: http://tinkerpop.com#3 ?p ?o ?g");
        results = sc.getStatements(vf.createURI("http://tinkerpop.com#3"), null, null, false);
        while(results.hasNext()) {
            System.out.println(results.next());
        }


        SPARQLParser parser = new SPARQLParser();
        CloseableIteration<? extends BindingSet, QueryEvaluationException> sparqlResults;
        String queryString = "SELECT ?x ?y WHERE { ?x <http://tinkerpop.com#knows> ?y }";
        ParsedQuery query = parser.parseQuery(queryString, "http://tinkerPop.com");

        System.out.println("\nSPARQL: " + queryString);
        sparqlResults = sc.evaluate(query.getTupleExpr(), query.getDataset(), new EmptyBindingSet(), false);
        while (sparqlResults.hasNext()) {
            System.out.println(sparqlResults.next());
        }


        // ANOTHER EXAMPLE

        Resource beijing = new URIImpl("http://example1.org/things/Beijing");
        Resource city = new URIImpl("http://example1.org/terms/city");
        Resource place = new URIImpl("http://example1.org/terms/place");

        Sail reasoner = new ForwardChainingRDFSInferencer(new GraphSail(graph));
        reasoner.initialize();

        try {
            SailConnection c = reasoner.getConnection();
            try {
                c.addStatement(city, RDFS.SUBCLASSOF, place);
                c.addStatement(beijing, RDF.TYPE, city);
                c.commit();

                CloseableIteration<? extends Statement, SailException>
                        = c.getStatements(beijing, null, null, true);
                try {
                    while (i.hasNext()) {
                        System.out.println("statement " + i.next());
                    }
                } finally {
                    i.close();
                }
            } finally {
                c.close();
            }
        } finally {
            reasoner.shutDown();
        }

        graph.shutdown();

        sc.close();
        graph.shutdown();
        sail.shutDown();

    }
}
