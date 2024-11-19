import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';
import { matchPasswordValidator } from '../../../../core/validators/password.validator';
import { noWhitespaceValidator } from '../../../../core/validators/names.validator';

@Component({
  selector: 'app-admin-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './admin-register.component.html',
  styleUrls: ['./admin-register.component.scss']
})
export class AdminRegisterComponent implements OnInit {
  registerForm: FormGroup;
  isSubmitting = false;
  showPassword = false;
  showConfirmPassword = false;

  // List of services for managers
  services = [
    'IMOC', 
    'SICO', 
    'Fusion Team', 
    'Digital Factory', 
    'Innovation Lab', 
    'Commercial Strategy'
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2), noWhitespaceValidator]],
      lastName: ['', [Validators.required, Validators.minLength(2), noWhitespaceValidator]],
      email: ['', [Validators.required, Validators.email]],
      username: ['', [Validators.required, Validators.minLength(2), noWhitespaceValidator]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,}$/)
      ]],
      confirmPassword: ['', [Validators.required]],
      role: ['', [Validators.required]],
      service: [''] // Optional service for managers
    }, {
      validators: [
        matchPasswordValidator('password', 'confirmPassword'),
        this.managerServiceValidator()
      ]
    });

    // Dynamically show/hide service field based on role
    this.registerForm.get('role')?.valueChanges.subscribe(role => {
      const serviceControl = this.registerForm.get('service');
      if (role === 'MANAGER') {
        serviceControl?.setValidators([Validators.required]);
      } else {
        serviceControl?.clearValidators();
      }
      serviceControl?.updateValueAndValidity();
    });
  }

  // Custom validator to require service when role is MANAGER
  managerServiceValidator() {
    return (form: FormGroup) => {
      const role = form.get('role')?.value;
      const service = form.get('service')?.value;
      
      return (role === 'MANAGER' && !service) 
        ? { managerServiceRequired: true } 
        : null;
    };
  }

  ngOnInit(): void {}

  async onSubmit(): Promise<void> {
    if (this.registerForm.valid) {
      this.isSubmitting = true;

      try {
        const formData = {
          appUser: {
            username: this.registerForm.value.username,
            email: this.registerForm.value.email,
            password: this.registerForm.value.password,
            name: this.registerForm.value.lastName,
            firstName: this.registerForm.value.firstName
          },
          role: {
            name: this.registerForm.value.role,
            service: this.registerForm.value.role === 'MANAGER' 
              ? this.registerForm.value.service 
              : null
          }
        };

        await this.authService.register(formData).toPromise();
        this.router.navigate(['/admin/users'], {
          queryParams: { registered: 'success' }
        });
      } catch (error: any) {
        console.error('Registration error:', error);
        // Handle specific error cases
      } finally {
        this.isSubmitting = false;
      }
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(this.registerForm.controls).forEach(key => {
        const control = this.registerForm.get(key);
        if (control?.invalid) {
          control.markAsTouched();
        }
      });
    }
  }

  

  // Password visibility toggle
  togglePasswordVisibility(field: 'password' | 'confirmPassword'): void {
    if (field === 'password') {
      this.showPassword = !this.showPassword;
    } else {
      this.showConfirmPassword = !this.showConfirmPassword;
    }
  }

  // Error message getters (similar to previous implementation)
  // ... (error message methods would be similar to the previous component)
}