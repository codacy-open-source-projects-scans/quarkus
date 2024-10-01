package io.quarkus.qute.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import io.quarkus.builder.item.SimpleBuildItem;
import io.quarkus.qute.Expression;
import io.quarkus.qute.ParameterDeclaration;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateNode;

/**
 * Represents the result of analysis of all templates.
 */
public final class TemplatesAnalysisBuildItem extends SimpleBuildItem {

    private final List<TemplateAnalysis> analysis;

    public TemplatesAnalysisBuildItem(List<TemplateAnalysis> analysis) {
        this.analysis = analysis;
    }

    public List<TemplateAnalysis> getAnalysis() {
        return analysis;
    }

    /**
     * Analysis of a particular template found in the given path.
     */
    public static final class TemplateAnalysis {

        // A user-defined id; may be null
        public final String id;

        // The id generated by the parser
        public final String generatedId;

        public final List<Expression> expressions;

        public final List<ParameterDeclaration> parameterDeclarations;

        // File path, e.g. hello.html or ItemResource/items.html
        public final String path;

        public final Set<String> fragmentIds;

        // Parsed template; should never be used directly
        private final Template template;

        TemplateAnalysis(String id, Template template, String path) {
            this.id = id;
            this.generatedId = template.getGeneratedId();
            this.expressions = template.getExpressions();
            this.parameterDeclarations = template.getParameterDeclarations();
            this.path = path;
            this.fragmentIds = template.getFragmentIds();
            this.template = template;
        }

        /**
         *
         * @return the child nodes of the root node
         * @see Template#getNodes()
         */
        public List<TemplateNode> getNodes() {
            return template.getNodes();
        }

        /**
         *
         * @return the collection of nodes that match the given predicate
         * @see Template#findNodes(Predicate)
         */
        public Collection<TemplateNode> findNodes(Predicate<TemplateNode> predicate) {
            return template.findNodes(predicate);
        }

        /**
         * Non-synthetic declarations go first, then sorted by the line.
         *
         * @return the sorted list of parameter declarations
         */
        public List<ParameterDeclaration> getSortedParameterDeclarations() {
            return getSortedParameterDeclarations(parameterDeclarations);
        }

        /**
         * Non-synthetic declarations go first, then sorted by the line.
         *
         * @return the sorted list of parameter declarations
         */
        public static List<ParameterDeclaration> getSortedParameterDeclarations(
                List<ParameterDeclaration> parameterDeclarations) {
            List<ParameterDeclaration> ret = new ArrayList<>(parameterDeclarations);
            ret.sort(new Comparator<ParameterDeclaration>() {
                @Override
                public int compare(ParameterDeclaration pd1, ParameterDeclaration pd2) {
                    int ret = Boolean.compare(pd1.getOrigin().isSynthetic(), pd2.getOrigin().isSynthetic());
                    return ret == 0 ? Integer.compare(pd1.getOrigin().getLine(), pd2.getOrigin().getLine()) : ret;
                }
            });
            return ret;
        }

        Expression findExpression(int id) {
            for (Expression expression : expressions) {
                if (expression.getGeneratedId() == id) {
                    return expression;
                }
            }
            return null;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((generatedId == null) ? 0 : generatedId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TemplateAnalysis other = (TemplateAnalysis) obj;
            return Objects.equals(generatedId, other.generatedId);
        }

    }

}