import { ComponentFixture, TestBed } from '@angular/core/testing';

import {TablaTransacciones} from './tabla-transacciones';

describe('TablaTransacciones', () => {
  let component: TablaTransacciones;
  let fixture: ComponentFixture<TablaTransacciones>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TablaTransacciones],
    }).compileComponents();

    fixture = TestBed.createComponent(TablaTransacciones);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
