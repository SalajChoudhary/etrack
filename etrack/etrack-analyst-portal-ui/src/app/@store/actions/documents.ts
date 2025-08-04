import { Action } from '@ngrx/store';
import { Document } from '../models/documents';
import { SEARCH, SEARCH_COMPLETE } from '../types/documents';

export class SearchAction implements Action {
  readonly type = SEARCH;

  constructor(public payload: string) {}
}

export class SearchCompleteAction implements Action {
  readonly type = SEARCH_COMPLETE;

  constructor(public payload: Document[]) {}
}

export type Actions
  = SearchAction
  | SearchCompleteAction;
