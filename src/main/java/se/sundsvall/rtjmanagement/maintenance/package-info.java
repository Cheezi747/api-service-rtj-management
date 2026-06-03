/**
 * Maintenance module — scheduled housekeeping jobs.
 *
 * Currently hosts the nightly demo reset ({@code DatabaseCleanupScheduler}), which wipes all
 * transactional data so the demo starts from a clean slate each morning. The module depends only
 * on Spring infrastructure ({@code JdbcTemplate}) and on no other application module, so it stays
 * fully isolated under Modulith boundary verification.
 */
@ApplicationModule(displayName = "Maintenance")
package se.sundsvall.rtjmanagement.maintenance;

import org.springframework.modulith.ApplicationModule;
