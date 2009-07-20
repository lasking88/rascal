package org.meta_environment.rascal.interpreter.matching;

import org.eclipse.imp.pdb.facts.INode;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.meta_environment.rascal.interpreter.EvaluatorContext;
import org.meta_environment.rascal.interpreter.Names;
import org.meta_environment.rascal.interpreter.env.Environment;

/* package */ class NodePattern extends AbstractPattern {
	private org.meta_environment.rascal.ast.QualifiedName name;
	private java.util.List<AbstractPattern> children;
	private INode treeSubject;
	private boolean firstMatch = false;
	private boolean debug = false;
	private final TypeFactory tf = TypeFactory.getInstance();
	
	NodePattern(IValueFactory vf, EvaluatorContext ctx, org.meta_environment.rascal.ast.QualifiedName qualifiedName, java.util.List<AbstractPattern> children){
		super(vf, ctx);
		this.name = qualifiedName;
		this.children = children;
		if(debug){
			System.err.println("AbstractPatternNode: " + name + ", #children: " + children.size() );
			System.err.println(name.getTree());
			for(AbstractPattern ap : children){
				System.err.println(ap);
			}
		}
	}
	
	@Override
	public void initMatch(IValue subject, Environment env){
		super.initMatch(subject, env);
		hasNext = false;
		if(!(subject.getType().isNodeType() || subject.getType().isAbstractDataType())){
			return;
		}
		treeSubject = (INode) subject;
		if(debug){
			System.err.println("AbstractPatternNode: pattern=" + name.getTree());
			System.err.println("AbstractPatternNode: treeSubject=" + treeSubject);
			System.err.println("AbstractPatternNode: treeSubject.arity() =" + treeSubject.arity());
			System.err.println("AbstractPatternNode: children.size() =" + children.size());
		}
		if(treeSubject.arity() != children.size()){
			return;
		}
		if(!Names.name(Names.lastName(name)).equals(treeSubject.getName().toString()))
				return;
		
		for (int i = 0; i < children.size(); i++){
			children.get(i).initMatch(treeSubject.get(i), env);
		}
		firstMatch = hasNext = true;
	}
	
	@Override
	public Type getType(Environment env) {
		 Type[] types = new Type[children.size()];

		 for (int i = 0; i < children.size(); i++) {
			 types[i] =  children.get(i).getType(env);
		 }
		 
		 Type signature = tf.tupleType(types);
		 if (env.isTreeConstructorName(name, signature)) {
			 return env.getConstructor(Names.name(Names.lastName(name)), signature); //.getAbstractDataType();
		 }
	     return tf.nodeType();
	}
	
	@Override
	public IValue toIValue(Environment env){
		Type[] types = new Type[children.size()];
		IValue[] vals = new IValue[children.size()];
		
		for (int i = 0; i < children.size(); i++) {
			types[i] =  children.get(i).getType(env);
			vals[i] =  children.get(i).toIValue(env);
		}
		Type signature = tf.tupleType(types);
		
		if(env.isTreeConstructorName(name, signature)){
			Type consType = env.getConstructor(name.toString(), signature);
			
			return vf.constructor(consType, vals);
		}
		return vf.node(name.toString(), vals);
	}

	@Override
	public java.util.List<String> getVariables(){
		java.util.LinkedList<String> res = new java.util.LinkedList<String> ();
		for (int i = 0; i < children.size(); i++) {
			res.addAll(children.get(i).getVariables());
		 }
		return res;
	}
	
	@Override
	public boolean hasNext(){
		if(!initialized)
			return false;
		if(firstMatch)
			return true;
		if(!hasNext)
			return false;
		if(children.size() > 0){
			for (int i = 0; i < children.size(); i++) {
				if(children.get(i).hasNext()){
					return true;
				}
			}
		}
		hasNext = false;
		return false;
	}
	
	@Override
	public boolean next(){
		checkInitialized();
		
		if(!(firstMatch || hasNext))
			return false;
		firstMatch = false;

		hasNext = matchChildren(treeSubject.getChildren().iterator(), children.iterator(), env);

		return hasNext;
	}
	
	@Override
	public String toString(){
		StringBuilder res = new StringBuilder(name.toString()).append("(");
		String sep = "";
		for(MatchPattern mp : children){
			res.append(sep);
			sep = ", ";
			res.append(mp.toString());
		}
		res.append(")");
		return res.toString();
	}
}
