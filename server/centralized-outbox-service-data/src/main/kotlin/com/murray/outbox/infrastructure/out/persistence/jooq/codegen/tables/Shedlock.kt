/*
 * This file is generated by jOOQ.
 */
package com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables


import java.time.LocalDateTime
import java.util.function.Function

import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Name
import org.jooq.Record
import org.jooq.Records
import org.jooq.Row4
import org.jooq.Schema
import org.jooq.SelectField
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableOptions
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class Shedlock(
    alias: Name,
    child: Table<out Record>?,
    path: ForeignKey<out Record, com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.records.ShedlockRecord>?,
    aliased: Table<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.records.ShedlockRecord>?,
    parameters: Array<Field<*>?>?
): TableImpl<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.records.ShedlockRecord>(
    alias,
    com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.CentralOutbox.CENTRAL_OUTBOX,
    child,
    path,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.table()
) {
    companion object {

        /**
         * The reference instance of <code>central_outbox.shedlock</code>
         */
        val SHEDLOCK: Shedlock = Shedlock()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.records.ShedlockRecord> = com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.records.ShedlockRecord::class.java

    /**
     * The column <code>central_outbox.shedlock.name</code>.
     */
    val NAME: TableField<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.records.ShedlockRecord, String?> = createField(DSL.name("name"), SQLDataType.VARCHAR(64).nullable(false), this, "")

    /**
     * The column <code>central_outbox.shedlock.lock_until</code>.
     */
    val LOCK_UNTIL: TableField<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.records.ShedlockRecord, LocalDateTime?> = createField(DSL.name("lock_until"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "")

    /**
     * The column <code>central_outbox.shedlock.locked_at</code>.
     */
    val LOCKED_AT: TableField<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.records.ShedlockRecord, LocalDateTime?> = createField(DSL.name("locked_at"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "")

    /**
     * The column <code>central_outbox.shedlock.locked_by</code>.
     */
    val LOCKED_BY: TableField<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.records.ShedlockRecord, String?> = createField(DSL.name("locked_by"), SQLDataType.VARCHAR(255).nullable(false), this, "")

    private constructor(alias: Name, aliased: Table<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.records.ShedlockRecord>?): this(alias, null, null, aliased, null)
    private constructor(alias: Name, aliased: Table<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.records.ShedlockRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, aliased, parameters)

    /**
     * Create an aliased <code>central_outbox.shedlock</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>central_outbox.shedlock</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>central_outbox.shedlock</code> table reference
     */
    constructor(): this(DSL.name("shedlock"), null)

    constructor(child: Table<out Record>, key: ForeignKey<out Record, com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.records.ShedlockRecord>): this(Internal.createPathAlias(child, key), child, key, SHEDLOCK, null)
    override fun getSchema(): Schema? = if (aliased()) null else com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.CentralOutbox.CENTRAL_OUTBOX
    override fun getPrimaryKey(): UniqueKey<com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.tables.records.ShedlockRecord> = com.murray.outbox.infrastructure.`out`.persistence.jooq.codegen.keys.SHEDLOCK_PKEY
    override fun `as`(alias: String): Shedlock = Shedlock(DSL.name(alias), this)
    override fun `as`(alias: Name): Shedlock = Shedlock(alias, this)
    override fun `as`(alias: Table<*>): Shedlock = Shedlock(alias.getQualifiedName(), this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Shedlock = Shedlock(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Shedlock = Shedlock(name, null)

    /**
     * Rename this table
     */
    override fun rename(name: Table<*>): Shedlock = Shedlock(name.getQualifiedName(), null)

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------
    override fun fieldsRow(): Row4<String?, LocalDateTime?, LocalDateTime?, String?> = super.fieldsRow() as Row4<String?, LocalDateTime?, LocalDateTime?, String?>

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    fun <U> mapping(from: (String?, LocalDateTime?, LocalDateTime?, String?) -> U): SelectField<U> = convertFrom(Records.mapping(from))

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    fun <U> mapping(toType: Class<U>, from: (String?, LocalDateTime?, LocalDateTime?, String?) -> U): SelectField<U> = convertFrom(toType, Records.mapping(from))
}
