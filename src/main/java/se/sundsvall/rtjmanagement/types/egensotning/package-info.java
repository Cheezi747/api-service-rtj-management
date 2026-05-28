/**
 * Egensotning type module — chimney self-cleaning applications.
 *
 * Replaces the legacy Daedalos + Teams + Ritz workflow with a single dept44 service
 * driving an Operaton BPMN ({@code egensotning-process}). The type module itself is
 * minimal — it only contributes its slug + valid statuses + valid stakeholder roles
 * to the core registries. The actual workflow lives in the BPMN; the workers that
 * service it live in {@code api-service-operaton/operaton-workers-rtjmanagement}.
 */
@ApplicationModule(displayName = "Egensotning")
package se.sundsvall.rtjmanagement.types.egensotning;

import org.springframework.modulith.ApplicationModule;
