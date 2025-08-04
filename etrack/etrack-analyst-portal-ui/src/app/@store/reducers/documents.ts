import { Document } from '../models/documents';
import * as document from '../actions/documents';
import { SEARCH_COMPLETE } from '../types/documents';

export interface State {
  ids: string[];
  entities: { [id: string]: Document };
  selectedDocumentId: string | null;
}

export const initialState: State = {
  ids: [],
  entities: {},
  selectedDocumentId: null
};

export function reducer(state = initialState, action: document.Actions): State {
  switch (action.type) {
    case SEARCH_COMPLETE: {
      const document = action.payload;
      const newDocument = document.filter(
        document => !state.entities[document.id]
      );

      const newDocIds = newDocument.map(doc => doc.id);
      const newDocEntities = newDocument.reduce(
        (entities: { [id: string]: Document }, document: Document) => {
          return Object.assign(entities, {
            [document.id]: document
          });
        },
        {}
      );

      return {
        ids: [...state.ids, ...newDocIds],
        entities: Object.assign({}, state.entities, newDocEntities),
        selectedDocumentId: state.selectedDocumentId
      };
    }

    default: {
      return state;
    }
  }
}

export const getDocumentsIds = (state: State) => state.ids;
