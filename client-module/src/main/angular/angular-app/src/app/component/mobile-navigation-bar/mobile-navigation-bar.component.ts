import { Component } from '@angular/core';

type MobileNavigation = {
  icon: string;
  route: string;
  alt: string;
};

@Component({
  selector: 'app-mobile-navigation-bar',
  templateUrl: './mobile-navigation-bar.component.html',
  styleUrls: ['./mobile-navigation-bar.component.scss'],
})
export class MobileNavigationBarComponent {
  readonly NAVIGATIONS: MobileNavigation[] = [
    {
      icon: 'bi bi-person',
      route: 'm/home',
      alt: 'Profile',
    },
    {
      icon: 'bi bi-house',
      route: '',
      alt: 'Home',
    },
    {
      icon: 'bi bi-list',
      route: 'm/menu',
      alt: 'Menu',
    },
  ];
}
