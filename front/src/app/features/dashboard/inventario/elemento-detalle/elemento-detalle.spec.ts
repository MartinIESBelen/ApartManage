import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ElementoDetalle } from './elemento-detalle';

describe('ElementoDetalle', () => {
  let component: ElementoDetalle;
  let fixture: ComponentFixture<ElementoDetalle>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ElementoDetalle],
    }).compileComponents();

    fixture = TestBed.createComponent(ElementoDetalle);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
