import { createSelector } from 'reselect';
import * as fromDocuments from '../reducers/documents';
export interface State {
  documents: fromDocuments.State;
}
export const getDocumentState = (state: State) => state.documents;

export const getDocumentIds = createSelector(
  getDocumentState,
  fromDocuments.getDocumentsIds
);
