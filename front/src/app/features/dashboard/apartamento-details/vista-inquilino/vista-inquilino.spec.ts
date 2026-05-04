import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VistaInquilino } from './vista-inquilino';

describe('VistaInquilino', () => {
  let component: VistaInquilino;
  let fixture: ComponentFixture<VistaInquilino>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VistaInquilino],
    }).compileComponents();

    fixture = TestBed.createComponent(VistaInquilino);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
