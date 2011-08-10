/*******************************************************************************
 * Copyright (c) 2009-2011 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   * Jurgen J. Vinju - Jurgen.Vinju@cwi.nl - CWI
 *   * Tijs van der Storm - Tijs.van.der.Storm@cwi.nl
 *   * Paul Klint - Paul.Klint@cwi.nl - CWI
 *   * Mark Hills - Mark.Hills@cwi.nl (CWI)
 *   * Arnold Lankamp - Arnold.Lankamp@cwi.nl
 *******************************************************************************/
package org.rascalmpl.ast;


import org.eclipse.imp.pdb.facts.IConstructor;
import org.rascalmpl.interpreter.asserts.Ambiguous;
import org.eclipse.imp.pdb.facts.IValue;
import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.env.Environment;
import org.rascalmpl.interpreter.result.Result;

public abstract class TypeVar extends AbstractAST {
  public TypeVar(IConstructor node) {
    super(node);
  }

  
  public boolean hasBound() {
    return false;
  }

  public org.rascalmpl.ast.Type getBound() {
    throw new UnsupportedOperationException();
  }
  public boolean hasName() {
    return false;
  }

  public org.rascalmpl.ast.Name getName() {
    throw new UnsupportedOperationException();
  }

  static public class Ambiguity extends TypeVar {
    private final java.util.List<org.rascalmpl.ast.TypeVar> alternatives;
  
    public Ambiguity(IConstructor node, java.util.List<org.rascalmpl.ast.TypeVar> alternatives) {
      super(node);
      this.alternatives = java.util.Collections.unmodifiableList(alternatives);
    }
    
    @Override
    public Result<IValue> interpret(Evaluator __eval) {
      throw new Ambiguous(this.getTree());
    }
      
    @Override
    public org.eclipse.imp.pdb.facts.type.Type typeOf(Environment env) {
      throw new Ambiguous(this.getTree());
    }
    
    public java.util.List<org.rascalmpl.ast.TypeVar> getAlternatives() {
      return alternatives;
    }
    
    public <T> T accept(IASTVisitor<T> v) {
    	return v.visitTypeVarAmbiguity(this);
    }
  }

  

  
  public boolean isFree() {
    return false;
  }

  static public class Free extends TypeVar {
    // Production: sig("Free",[arg("org.rascalmpl.ast.Name","name")])
  
    
    private final org.rascalmpl.ast.Name name;
  
    public Free(IConstructor node , org.rascalmpl.ast.Name name) {
      super(node);
      
      this.name = name;
    }
  
    @Override
    public boolean isFree() { 
      return true; 
    }
  
    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
      return visitor.visitTypeVarFree(this);
    }
  
    
    @Override
    public org.rascalmpl.ast.Name getName() {
      return this.name;
    }
  
    @Override
    public boolean hasName() {
      return true;
    }	
  }
  public boolean isBounded() {
    return false;
  }

  static public class Bounded extends TypeVar {
    // Production: sig("Bounded",[arg("org.rascalmpl.ast.Name","name"),arg("org.rascalmpl.ast.Type","bound")])
  
    
    private final org.rascalmpl.ast.Name name;
    private final org.rascalmpl.ast.Type bound;
  
    public Bounded(IConstructor node , org.rascalmpl.ast.Name name,  org.rascalmpl.ast.Type bound) {
      super(node);
      
      this.name = name;
      this.bound = bound;
    }
  
    @Override
    public boolean isBounded() { 
      return true; 
    }
  
    @Override
    public <T> T accept(IASTVisitor<T> visitor) {
      return visitor.visitTypeVarBounded(this);
    }
  
    
    @Override
    public org.rascalmpl.ast.Name getName() {
      return this.name;
    }
  
    @Override
    public boolean hasName() {
      return true;
    }
    @Override
    public org.rascalmpl.ast.Type getBound() {
      return this.bound;
    }
  
    @Override
    public boolean hasBound() {
      return true;
    }	
  }
}