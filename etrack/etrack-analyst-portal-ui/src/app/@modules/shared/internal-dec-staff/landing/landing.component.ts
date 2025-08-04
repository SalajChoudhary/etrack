import { Component, OnInit } from '@angular/core';
import { AuthService } from "../../../../core/auth/auth.service";

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  styles: [
  ]
})
export class LandingComponent implements OnInit {
  loggedInUser!: string;
  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    this.loggedInUser = `${this.authService.getUserInfo().unique_name}`;
  }

}
