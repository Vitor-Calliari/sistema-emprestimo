import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmprestimoForm } from './emprestimo-form';

describe('EmprestimoForm', () => {
  let component: EmprestimoForm;
  let fixture: ComponentFixture<EmprestimoForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmprestimoForm],
    }).compileComponents();

    fixture = TestBed.createComponent(EmprestimoForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
