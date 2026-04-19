import {Component, inject, signal} from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import {filter, map} from 'rxjs';
import {HeaderComponent} from './shared/header/header';
import {FooterComponent} from './shared/footer/footer';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, FooterComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private router = inject(Router);

  mostrarLayout = toSignal(
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      map((event: any) => {
        const url = event.urlAfterRedirects;
        // Si la URL contiene login o register, devolvemos false
        return !url.includes('/login') && !url.includes('/register');
      })
    ),
    { initialValue: true }
  );
  protected readonly title = signal('front');
}
