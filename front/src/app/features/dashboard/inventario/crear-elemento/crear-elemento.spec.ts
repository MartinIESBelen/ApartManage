import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrearElemento } from './crear-elemento';

describe('CrearElemento', () => {
  let component: CrearElemento;
  let fixture: ComponentFixture<CrearElemento>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CrearElemento],
    }).compileComponents();

    fixture = TestBed.createComponent(CrearElemento);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
