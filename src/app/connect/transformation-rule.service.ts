import {Injectable} from '@angular/core';
import {EventSchema} from './schema-editor/model/EventSchema';
import {TransformationRuleDescription} from './model/rules/TransformationRuleDescription';
import {Logger} from '../shared/logger/default-log.service';
import {RenameRuleDescription} from './model/rules/RenameRuleDescription';
import {EventProperty} from './schema-editor/model/EventProperty';
import {EventPropertyPrimitive} from './schema-editor/model/EventPropertyPrimitive';
import {EventPropertyNested} from './schema-editor/model/EventPropertyNested';
import {AddNestedRuleDescription} from './model/rules/AddNestedRuleDescription';
import {k} from '@angular/core/src/render3';

@Injectable()
export class TransformationRuleService {

    private oldEventSchema: EventSchema = null;
    private newEventSchema: EventSchema = null;

    constructor(public logger: Logger) {}

    public setOldEventSchema(oldEventSchema: EventSchema) {
        this.oldEventSchema = oldEventSchema;
    }

    public setNewEventSchema(newEventSchema: EventSchema) {
        this.newEventSchema = newEventSchema;
    }

    public getTransformationRuleDescriptions(): TransformationRuleDescription[] {
        if (this.oldEventSchema == null || this.newEventSchema == null) {
            this.logger.error("Old and new schema must be defined")
        }

        var transformationRuleDescription: TransformationRuleDescription[] = [];

        // Rename [implemented]
        transformationRuleDescription = transformationRuleDescription.concat(this.getRenameRules(
            this.newEventSchema.eventProperties, this.newEventSchema, this.oldEventSchema));


        // Create Nested [implemented]
        transformationRuleDescription = transformationRuleDescription.concat(this.getCreateNestedRules(
            this.newEventSchema.eventProperties, this.newEventSchema, this.oldEventSchema));

        // Move []


        // Delete



        return transformationRuleDescription;
    }

    public getCreateNestedRules(newEventProperties: EventProperty[],
                          oldEventSchema: EventSchema,
                          newEventSchema: EventSchema): AddNestedRuleDescription[] {


        var allNewIds: string[] = this.getAllIds(newEventSchema.eventProperties);
        var allOldIds: string[] = this.getAllIds(oldEventSchema.eventProperties);

        const result: AddNestedRuleDescription[] = [];
        for (let id of allNewIds) {
            if (allOldIds.indexOf(id) === -1) {
                const key = this.getCompleteRuntimeNameKey(newEventSchema.eventProperties, id);
                result.push(new AddNestedRuleDescription(key));
            }
        }

        return result;
    }


    public getRenameRules(newEventProperties: EventProperty[],
                          oldEventSchema: EventSchema,
                          newEventSchema: EventSchema): RenameRuleDescription[] {

        var result: RenameRuleDescription[] = [];

        for (let eventProperty of newEventProperties) {
            var keyOld = this.getCompleteRuntimeNameKey(oldEventSchema.eventProperties, eventProperty.id);
            var keyNew = this.getCompleteRuntimeNameKey(newEventSchema.eventProperties, eventProperty.id);

            result.push(new RenameRuleDescription(keyOld, keyNew));
            if (eventProperty instanceof EventPropertyNested) {

                const tmpResults: RenameRuleDescription[] = this.getRenameRules((<EventPropertyNested> eventProperty).eventProperties, oldEventSchema, newEventSchema);
                result = result.concat(tmpResults);

            }
        }

        var filteredResult: RenameRuleDescription[] = [];
        for (let res of result) {
            if (res.newRuntimeKey != res.oldRuntimeKey) {
                filteredResult.push(res);
            }
        }

        return filteredResult;
    }

    public getCompleteRuntimeNameKey(eventProperties: EventProperty[], id: string): string {
        var result: string = '';

        for (let eventProperty of eventProperties) {

            if (eventProperty.id === id) {
                return eventProperty.getRuntimeName();
            } else {
                if (eventProperty instanceof EventPropertyNested) {
                    var methodResult = this.getCompleteRuntimeNameKey((<EventPropertyNested> eventProperty).eventProperties, id);
                    if (methodResult != null) {
                        result = eventProperty.getRuntimeName() + "." + methodResult;
                    }
                }
            }
        }

        if (result == '') {
            return null;
        } else {
            return result;
        }
    }


    public getAllIds(eventProperties: EventProperty[]): string[] {
        var result: string[] = [];

        for (let eventProperty of eventProperties) {
            result.push(eventProperty.id);

            if (eventProperty instanceof EventPropertyNested) {
                result = result.concat(this.getAllIds((<EventPropertyNested> eventProperty).eventProperties));
            }
        }
        return result;
    }


}