/*
 * Copyright 1999-2012 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * (created at 2011-5-30)
 */
package org.lysu.shard.parser.visitor;


import org.lysu.shard.parser.ast.expression.BinaryOperatorExpression;
import org.lysu.shard.parser.ast.expression.PolyadicOperatorExpression;
import org.lysu.shard.parser.ast.expression.UnaryOperatorExpression;
import org.lysu.shard.parser.ast.expression.comparison.*;
import org.lysu.shard.parser.ast.expression.logical.LogicalAndExpression;
import org.lysu.shard.parser.ast.expression.logical.LogicalOrExpression;
import org.lysu.shard.parser.ast.expression.misc.InExpressionList;
import org.lysu.shard.parser.ast.expression.misc.UserExpression;
import org.lysu.shard.parser.ast.expression.primary.*;
import org.lysu.shard.parser.ast.expression.primary.function.FunctionExpression;
import org.lysu.shard.parser.ast.expression.primary.function.cast.Cast;
import org.lysu.shard.parser.ast.expression.primary.function.cast.Convert;
import org.lysu.shard.parser.ast.expression.primary.function.datetime.Extract;
import org.lysu.shard.parser.ast.expression.primary.function.datetime.GetFormat;
import org.lysu.shard.parser.ast.expression.primary.function.datetime.Timestampadd;
import org.lysu.shard.parser.ast.expression.primary.function.datetime.Timestampdiff;
import org.lysu.shard.parser.ast.expression.primary.function.groupby.*;
import org.lysu.shard.parser.ast.expression.primary.function.string.Char;
import org.lysu.shard.parser.ast.expression.primary.function.string.Trim;
import org.lysu.shard.parser.ast.expression.primary.literal.*;
import org.lysu.shard.parser.ast.expression.string.LikeExpression;
import org.lysu.shard.parser.ast.expression.type.CollateExpression;
import org.lysu.shard.parser.ast.fragment.GroupBy;
import org.lysu.shard.parser.ast.fragment.Limit;
import org.lysu.shard.parser.ast.fragment.OrderBy;
import org.lysu.shard.parser.ast.fragment.ddl.ColumnDefinition;
import org.lysu.shard.parser.ast.fragment.ddl.TableOptions;
import org.lysu.shard.parser.ast.fragment.ddl.datatype.DataType;
import org.lysu.shard.parser.ast.fragment.ddl.index.IndexColumnName;
import org.lysu.shard.parser.ast.fragment.ddl.index.IndexOption;
import org.lysu.shard.parser.ast.fragment.tableref.*;
import org.lysu.shard.parser.ast.stmt.dal.*;
import org.lysu.shard.parser.ast.stmt.ddl.*;
import org.lysu.shard.parser.ast.stmt.dml.*;
import org.lysu.shard.parser.ast.stmt.extension.ExtDDLCreatePolicy;
import org.lysu.shard.parser.ast.stmt.extension.ExtDDLDropPolicy;
import org.lysu.shard.parser.ast.stmt.mts.MTSReleaseStatement;
import org.lysu.shard.parser.ast.stmt.mts.MTSRollbackStatement;
import org.lysu.shard.parser.ast.stmt.mts.MTSSavepointStatement;
import org.lysu.shard.parser.ast.stmt.mts.MTSSetTransactionStatement;

/**
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public interface SQLASTVisitor {

    void visit(BetweenAndExpression node);

    void visit(ComparisionIsExpression node);

    void visit(InExpressionList node);

    void visit(LikeExpression node);

    void visit(CollateExpression node);

    void visit(UserExpression node);

    void visit(UnaryOperatorExpression node);

    void visit(BinaryOperatorExpression node);

    void visit(PolyadicOperatorExpression node);

    void visit(LogicalAndExpression node);

    void visit(LogicalOrExpression node);

    void visit(ComparisionEqualsExpression node);

    void visit(ComparisionNullSafeEqualsExpression node);

    void visit(InExpression node);

    // -------------------------------------------------------
    void visit(FunctionExpression node);

    void visit(Char node);

    void visit(Convert node);

    void visit(Trim node);

    void visit(Cast node);

    void visit(Avg node);

    void visit(Max node);

    void visit(Min node);

    void visit(Sum node);

    void visit(Count node);

    void visit(GroupConcat node);

    void visit(Extract node);

    void visit(Timestampdiff node);

    void visit(Timestampadd node);

    void visit(GetFormat node);

    // -------------------------------------------------------
    void visit(IntervalPrimary node);

    void visit(LiteralBitField node);

    void visit(LiteralBoolean node);

    void visit(LiteralHexadecimal node);

    void visit(LiteralNull node);

    void visit(LiteralNumber node);

    void visit(LiteralString node);

    void visit(CaseWhenOperatorExpression node);

    void visit(DefaultValue node);

    void visit(ExistsPrimary node);

    void visit(PlaceHolder node);

    void visit(Identifier node);

    void visit(MatchExpression node);

    void visit(ParamMarker node);

    void visit(RowExpression node);

    void visit(SysVarPrimary node);

    void visit(UsrDefVarPrimary node);

    // -------------------------------------------------------
    void visit(IndexHint node);

    void visit(InnerJoin node);

    void visit(NaturalJoin node);

    void visit(OuterJoin node);

    void visit(StraightJoin node);

    void visit(SubqueryFactor node);

    void visit(TableReferences node);

    void visit(TableRefFactor node);

    void visit(Dual dual);

    void visit(GroupBy node);

    void visit(Limit node);

    void visit(OrderBy node);

    void visit(ColumnDefinition node);

    void visit(IndexOption node);

    void visit(IndexColumnName node);

    void visit(TableOptions node);

    void visit(DDLAlterTableStatement.AlterSpecification node);

    void visit(DataType node);

    // -------------------------------------------------------
    void visit(ShowAuthors node);

    void visit(ShowBinaryLog node);

    void visit(ShowBinLogEvent node);

    void visit(ShowCharaterSet node);

    void visit(ShowCollation node);

    void visit(ShowColumns node);

    void visit(ShowContributors node);

    void visit(ShowCreate node);

    void visit(ShowDatabases node);

    void visit(ShowEngine node);

    void visit(ShowEngines node);

    void visit(ShowErrors node);

    void visit(ShowEvents node);

    void visit(ShowFunctionCode node);

    void visit(ShowFunctionStatus node);

    void visit(ShowGrants node);

    void visit(ShowIndex node);

    void visit(ShowMasterStatus node);

    void visit(ShowOpenTables node);

    void visit(ShowPlugins node);

    void visit(ShowPrivileges node);

    void visit(ShowProcedureCode node);

    void visit(ShowProcedureStatus node);

    void visit(ShowProcesslist node);

    void visit(ShowProfile node);

    void visit(ShowProfiles node);

    void visit(ShowSlaveHosts node);

    void visit(ShowSlaveStatus node);

    void visit(ShowStatus node);

    void visit(ShowTables node);

    void visit(ShowTableStatus node);

    void visit(ShowTriggers node);

    void visit(ShowVariables node);

    void visit(ShowWarnings node);

    void visit(DescTableStatement node);

    void visit(DALSetStatement node);

    void visit(DALSetNamesStatement node);

    void visit(DALSetCharacterSetStatement node);

    // -------------------------------------------------------
    void visit(DMLCallStatement node);

    void visit(DMLDeleteStatement node);

    void visit(DMLInsertStatement node);

    void visit(DMLReplaceStatement node);

    void visit(DMLSelectStatement node);

    void visit(DMLSelectUnionStatement node);

    void visit(DMLUpdateStatement node);

    void visit(MTSSetTransactionStatement node);

    void visit(MTSSavepointStatement node);

    void visit(MTSReleaseStatement node);

    void visit(MTSRollbackStatement node);

    void visit(DDLTruncateStatement node);

    void visit(DDLAlterTableStatement node);

    void visit(DDLCreateIndexStatement node);

    void visit(DDLCreateTableStatement node);

    void visit(DDLRenameTableStatement node);

    void visit(DDLDropIndexStatement node);

    void visit(DDLDropTableStatement node);

    void visit(ExtDDLCreatePolicy node);

    void visit(ExtDDLDropPolicy node);

}
