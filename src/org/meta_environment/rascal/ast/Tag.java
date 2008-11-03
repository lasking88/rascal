package org.meta_environment.rascal.ast;

import org.eclipse.imp.pdb.facts.ITree;

public abstract class Tag extends AbstractAST {
	static public class Ambiguity extends Tag {
		private final java.util.List<org.meta_environment.rascal.ast.Tag> alternatives;

		public Ambiguity(
				java.util.List<org.meta_environment.rascal.ast.Tag> alternatives) {
			this.alternatives = java.util.Collections
					.unmodifiableList(alternatives);
		}

		public java.util.List<org.meta_environment.rascal.ast.Tag> getAlternatives() {
			return alternatives;
		}
	}

	static public class Default extends Tag {
		private org.meta_environment.rascal.ast.Name name;

		/* "@" name:Name TagString -> Tag {cons("Default")} */
		private Default() {
		}

		/* package */Default(ITree tree,
				org.meta_environment.rascal.ast.Name name) {
			this.tree = tree;
			this.name = name;
		}

		private void $setName(org.meta_environment.rascal.ast.Name x) {
			this.name = x;
		}

		public IVisitable accept(IASTVisitor visitor) {
			return visitor.visitTagDefault(this);
		}

		public org.meta_environment.rascal.ast.Name getName() {
			return name;
		}

		public Default setName(org.meta_environment.rascal.ast.Name x) {
			Default z = new Default();
			z.$setName(x);
			return z;
		}
	}
}
