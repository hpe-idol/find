/*
 * Copyright 2016 Hewlett-Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

define([
    'backbone',
    'underscore',
    'i18n!find/nls/bundle',
    'i18n!find/idol/nls/snapshots',
    'find/app/util/collapsible',
    './data-panel-view',
    './snapshot-detail',
    './snapshot-restrictions'
], function(Backbone, _, i18n, snapshotsI18n, Collapsible, DataPanelView,
            snapshotDetail, snapshotRestrictions) {
    'use strict';

    /**
     * Contents of the left side panel for a saved snapshot.
     * @name SnapshotDataView
     * @constructor
     */
    return Backbone.View.extend({
        initialize: function(options) {
            this.collapsibles = [
                new Collapsible({
                    title: snapshotsI18n['detailTitle'],
                    view: new DataPanelView(_.extend({
                        model: options.savedSearchModel
                    }, snapshotDetail))
                }),
                new Collapsible({
                    title: snapshotsI18n['restrictionsTitle'],
                    view: new DataPanelView(_.extend({
                        model: options.savedSearchModel
                    }, snapshotRestrictions))
                })
            ];
        },

        render: function() {
            this.$el.empty();

            _.each(this.collapsibles, function(view) {
                this.$el.append(view.$el);
                view.render();
            }, this);
        },

        remove: function() {
            _.invoke(this.collapsibles, 'remove');
            Backbone.View.prototype.remove.call(this);
        }
    });
});
