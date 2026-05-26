/**
 * Notifications module — listens to errand events, dispatches notifications.
 *
 * Subscribes to events from core and other modules via {@code @ApplicationModuleListener}
 * (see {@code service/event/NotificationEventListener}).
 */
@ApplicationModule(displayName = "Notifications")
package se.sundsvall.rtjmanagement.notifications;

import org.springframework.modulith.ApplicationModule;
