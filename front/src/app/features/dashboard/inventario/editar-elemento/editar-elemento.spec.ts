import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditarElemento } from './editar-elemento';

describe('EditarElemento', () => {
  let component: EditarElemento;
  let fixture: ComponentFixture<EditarElemento>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditarElemento],
    }).compileComponents();

    fixture = TestBed.createComponent(EditarElemento);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
