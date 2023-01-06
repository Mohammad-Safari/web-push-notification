import { Component, Input, Renderer2 } from '@angular/core';
import { ConnectedPosition, Overlay } from '@angular/cdk/overlay';

type Navigation = {
  title: string;
  route?: string;
  subNavigation?: Navigation[];
};

@Component({
  selector: 'app-navigation-bar',
  templateUrl: './navigation-bar.component.html',
  styleUrls: ['./navigation-bar.component.scss'],
})
export class NavigationBarComponent {
  @Input() name: string | null;
  @Input() title: string;

  constructor(public renderer: Renderer2) {}

  readonly NAVIGATIONS: Navigation[] = [
    {
      title: 'Account',
      subNavigation: [
        { title: 'Sign Up', route: '/signup' },
        { title: 'Login', route: '/login' },
      ],
    },
    {
      title: 'Notification',
      subNavigation: [
        { title: 'Notification', route: '/notification' },
        { title: '3rd Party Notification', route: '/notification3rdp' },
        { title: 'socket messaging', route: '/messaging' },
      ],
    },
  ];
  readonly POSITIONS: ConnectedPosition[] = [
    {
      originX: 'start',
      originY: 'bottom',
      overlayX: 'center',
      overlayY: 'top',
    },
  ];

  toggleNavigationItem(item: HTMLLIElement, attribute: string) {
    item.hasAttribute(attribute)
      ? item.removeAttribute(attribute)
      : this.setUniqueItemAttributeAmongSiblings(item, attribute);
  }

  openSubNavigation(item: HTMLLIElement, attribute: string) {
    this.setUniqueItemAttributeAmongSiblings(item, attribute);
      this.renderer.listen(item, 'mouseleave', (event) => {
        const mousePresentInMenu = [
        event?.origianlTarget?.id,
        event?.explicitOriginalTarget?.id,
        event?.rangeParent?.id,
        event?.relatedTarget?.id].includes('dropdown-menu');
        if (!mousePresentInMenu) {
          item.removeAttribute(attribute);
        }
      });
  }

  private setUniqueItemAttributeAmongSiblings(
    item: HTMLElement,
    attribute: string
  ) {
    this.removeAllChildrenAttribute(item.parentElement, attribute);
    item.toggleAttribute(attribute);
  }

  private removeAllChildrenAttribute(
    parent: HTMLElement | null,
    attribute: string
  ) {
    const children = parent?.children ?? [];
    for (let i = 0; i < children?.length; i++) {
      children[i].removeAttribute(attribute);
    }
  }
}
