package org.meta_environment.rascal.interpreter.result;

import static org.meta_environment.rascal.interpreter.result.ResultFactory.bool;
import static org.meta_environment.rascal.interpreter.result.ResultFactory.makeResult;

import java.util.Iterator;
import java.util.Map.Entry;

import org.eclipse.imp.pdb.facts.IMap;
import org.eclipse.imp.pdb.facts.IMapWriter;
import org.eclipse.imp.pdb.facts.IRelationWriter;
import org.eclipse.imp.pdb.facts.ISetWriter;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.exceptions.UndeclaredFieldException;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeStore;
import org.meta_environment.rascal.interpreter.IEvaluatorContext;
import org.meta_environment.rascal.interpreter.staticErrors.UnexpectedTypeError;
import org.meta_environment.rascal.interpreter.staticErrors.UnsupportedOperationError;
import org.meta_environment.rascal.interpreter.staticErrors.UnsupportedSubscriptArityError;
import org.meta_environment.rascal.interpreter.utils.RuntimeExceptionFactory;

public class MapResult extends ElementResult<IMap> {
	
	public MapResult(Type type, IMap map, IEvaluatorContext ctx) {
		super(type, map, ctx);
	}
	
	@Override 
	public <U extends IValue, V extends IValue> Result<U> add(Result<V> result) {
		return result.addMap(this);
		
	}

	@Override 
	public <U extends IValue, V extends IValue> Result<U> subtract(Result<V> result) {
		return result.subtractMap(this);
		
	}
	

	@Override
	public <U extends IValue, V extends IValue> Result<U> intersect(Result<V> result) {
		return result.intersectMap(this);
	}
	

	@Override
	@SuppressWarnings("unchecked")
	public <U extends IValue, V extends IValue> Result<U> subscript(Result<?>[] subscripts) {
		if (subscripts.length != 1) {
			throw new UnsupportedSubscriptArityError(getType(), subscripts.length, ctx.getCurrentAST());
		}
		Result<IValue> key = (Result<IValue>) subscripts[0];
		if (!getType().getKeyType().comparable(key.getType())) {
			throw new UnexpectedTypeError(getType().getKeyType(), key.getType(), ctx.getCurrentAST());
		}
		IValue v = getValue().get(key.getValue());
		if (v == null){
			throw RuntimeExceptionFactory.noSuchKey(key.getValue(), ctx.getCurrentAST(), ctx.getStackTrace());
		}
		return makeResult(getType().getValueType(), v, ctx);
	}
	
	@Override
	public <U extends IValue, V extends IValue> Result<U> equals(Result<V> that) {
		return that.equalToMap(this);
	}

	@Override
	public <U extends IValue> Result<U> fieldAccess(String name, TypeStore store) {
		if (type.getKeyLabel().equals(name)) {
			ISetWriter w = getValueFactory().setWriter(type.getKeyType());
			w.insertAll(value);
			return makeResult(getTypeFactory().setType(type.getKeyType()), w.done(), ctx);
		}
		else if (type.getValueLabel().equals(name)) {
			ISetWriter w = getValueFactory().setWriter(type.getValueType());
			Iterator<IValue> it = value.valueIterator();
			while (it.hasNext()) {
				w.insert(it.next());
			}
			return makeResult(getTypeFactory().setType(type.getValueType()), w.done(), ctx);
		}
		
		throw new UndeclaredFieldException(type, name);
	}
	
	@Override
	public <U extends IValue, V extends IValue> Result<U> fieldUpdate(
			String name, Result<V> repl, TypeStore store) {
		if (type.getKeyLabel().equals(name)) {
			throw new UnsupportedOperationError("You can not update the keys of a map using the field update operator", ctx.getCurrentAST());
		}
		else if (type.getValueLabel().equals(name)) {
			// interesting operation, sets the image of all keys to one default
			if (!repl.getType().isSubtypeOf(type.getValueType())) {
				throw new UnexpectedTypeError(type.getValueType(), repl.getType(), ctx.getCurrentAST());
			}

			IMapWriter w = getValueFactory().mapWriter(type.getKeyType(), type.getValueType());
			
			for (IValue key : value) {
				w.put(key, repl.getValue());
			}
			
			return makeResult(type, w.done(), ctx);
		}
		
		throw new UndeclaredFieldException(type, name);
	}
	
	@Override
	public <U extends IValue, V extends IValue> Result<U> nonEquals(Result<V> that) {
		return that.nonEqualToMap(this);
	}

	@Override
	public <U extends IValue, V extends IValue> Result<U> lessThan(Result<V> that) {
		return that.lessThanMap(this);
	}
	
	@Override
	public <U extends IValue, V extends IValue> Result<U> lessThanOrEqual(Result<V> that) {
		return that.lessThanOrEqualMap(this);
	}

	@Override
	public <U extends IValue, V extends IValue> Result<U> greaterThan(Result<V> that) {
		return that.greaterThanMap(this);
	}
	
	@Override
	public <U extends IValue, V extends IValue> Result<U> greaterThanOrEqual(Result<V> that) {
		return that.greaterThanOrEqualMap(this);
	}

	
	@Override
	public <U extends IValue, V extends IValue> Result<U> compare(Result<V> result) {
		return result.compareMap(this);
	}
	
	@Override
	public <U extends IValue, V extends IValue> Result<U> in(Result<V> result) {
		return result.inMap(this);
	}	
	
	@Override
	public <U extends IValue, V extends IValue> Result<U> notIn(Result<V> result) {
		return result.notInMap(this);
	}	
	
	////
	
	protected <U extends IValue, V extends IValue> Result<U> elementOf(ElementResult<V> elementResult) {
		return bool(getValue().containsKey(elementResult.getValue()), ctx);
	}

	protected <U extends IValue, V extends IValue> Result<U> notElementOf(ElementResult<V> elementResult) {
		return bool(!getValue().containsKey(elementResult.getValue()), ctx);
	}
	
	@Override
	protected <U extends IValue> Result<U> addMap(MapResult m) {
		// Note the reverse
		return makeResult(getType().lub(m.getType()), m.value.join(value), ctx);
	}
	
	@Override
	protected <U extends IValue> Result<U> subtractMap(MapResult m) {
		// Note the reverse
		return makeResult(m.getType(), m.getValue().remove(getValue()), ctx);
	}
	
	@Override
	protected <U extends IValue> Result<U> intersectMap(MapResult m) {
		// Note the reverse
		return makeResult(m.getType(), m.getValue().common(getValue()), ctx);
	}

	
	@Override
	protected <U extends IValue> Result<U> equalToMap(MapResult that) {
		return that.equalityBoolean(this);
	}
	
	@Override
	protected <U extends IValue> Result<U> nonEqualToMap(MapResult that) {
		return that.nonEqualityBoolean(this);
	}
	
	@Override
	protected <U extends IValue> Result<U> lessThanMap(MapResult that) {
		// note reversed args: we need that < this
		return bool(that.getValue().isSubMap(getValue()) && !that.getValue().isEqual(getValue()), ctx);
	}
	
	@Override
	protected <U extends IValue> Result<U> lessThanOrEqualMap(MapResult that) {
		// note reversed args: we need that <= this
		return bool(that.getValue().isSubMap(getValue()), ctx);
	}

	@Override
	protected <U extends IValue> Result<U> greaterThanMap(MapResult that) {
		// note reversed args: we need that > this
		return bool(getValue().isSubMap(that.getValue()) && !getValue().isEqual(that.getValue()), ctx);
	}
	
	@Override
	protected <U extends IValue> Result<U> greaterThanOrEqualMap(MapResult that) {
		// note reversed args: we need that >= this
		return bool(getValue().isSubMap(that.getValue()), ctx);
	}
	
	@Override
	protected <U extends IValue> Result<U> compareMap(MapResult that) {
		// Note reversed args
		IMap left = that.getValue();
		IMap right = this.getValue();
		// TODO: this is not right; they can be disjoint
		if (left.isEqual(right)) {
			return makeIntegerResult(0);
		}
		if (left.isSubMap(left)) {
			return makeIntegerResult(-1);
		}
		return makeIntegerResult(1);
	}
	
	@Override
	public <U extends IValue, V extends IValue> Result<U> compose(
			Result<V> right) {
		return right.composeMap(this);
	}
	
	@Override
	public <U extends IValue> Result<U> composeMap(MapResult left) {
		if (left.getType().getValueType().isSubtypeOf(getType().getKeyType())) {
			Type mapType = getTypeFactory().mapType(left.getType().getKeyType(), getType().getValueType());
			return ResultFactory.makeResult(mapType, left.getValue().compose(getValue()), ctx);
		}
		
		return undefinedError("composition", left);
	}
	
	@Override
	public Result<IValue> fieldSelect(int[] selectedFields) {
		IRelationWriter w = getValueFactory().relationWriter(type.getFieldTypes());
		
		// TODO: poor mans implementation can be made much faster without intermediate relation building
		Iterator<Entry<IValue,IValue>> it = value.entryIterator();
		while (it.hasNext()) {
			Entry<IValue,IValue> entry = it.next();
			w.insert(getValueFactory().tuple(entry.getKey(), entry.getValue()));
		}
		
		return makeResult(getTypeFactory().setType(type.getFieldTypes()), w.done(), ctx).fieldSelect(selectedFields);
	}
}
