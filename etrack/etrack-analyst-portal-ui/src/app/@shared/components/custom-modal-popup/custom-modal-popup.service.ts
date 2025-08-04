import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanDeactivate, RouterStateSnapshot, UrlTree } from "@angular/router";
import { Observable } from "rxjs";
import { CustomModalPopupComponent } from "./custom-modal-popup.component";

export interface ModalGuard{
    modalComponent: CustomModalPopupComponent
    shouldGuard?(): boolean
    excludeUrls?: string[]
}

@Injectable()
export class ModalGuardService implements CanDeactivate<ModalGuard> {
    constructor(){}
    canDeactivate(
        component: ModalGuard, 
        currentRoute: ActivatedRouteSnapshot, 
        currentState: RouterStateSnapshot, 
        nextState?: RouterStateSnapshot
    ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        // if(component.shouldGuard === undefined || component.shouldGuard())
        // {
        //     return component.modalComponent.open();
        // }
        return true;
    }
}