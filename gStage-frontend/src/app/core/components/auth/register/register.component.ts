import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { matchPasswordValidator } from '../../../validators/password.validator';
import { noWhitespaceValidator } from '../../../validators/names.validator';
import { PasswordCriteria } from '../../../interfaces/auth.interface';



@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './register.component.html',
  styleUrls: [
    './register.component.scss',
    '../auth.component.scss'
  ]
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  isLoading = false;
  isSubmitting = false;
  showPassword = false;
  showConfirmPassword = false;
  termsError = '';

  passwordCriteria: PasswordCriteria = {
    length: false,
    uppercase: false,
    lowercase: false,
    number: false,
    special: false
  };

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2), noWhitespaceValidator]],
      name: ['', [Validators.required, Validators.minLength(2), noWhitespaceValidator]],
      email: ['', [Validators.required, Validators.email]],
      username: ['', [Validators.required, Validators.minLength(2), noWhitespaceValidator]],

      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,}$/)
      ]],
      confirmPassword: ['', [Validators.required]],
      role: ['', [Validators.required]],
      acceptTerms: [false, [Validators.requiredTrue]]
    }, {
      validators: matchPasswordValidator('password', 'confirmPassword')
    });

    this.registerForm.get('password')?.valueChanges.subscribe(password => {
      this.updatePasswordCriteria(password);
    });
  }

  ngOnInit(): void {}

  updatePasswordCriteria(password: string): void {
    this.passwordCriteria = {
      length: password.length >= 8,
      uppercase: /[A-Z]/.test(password),
      lowercase: /[a-z]/.test(password),
      number: /[0-9]/.test(password),
      special: /[@$!%*?&#]/.test(password)
    };
  }

  // Getters pour les messages d'erreur
  get firstNameError(): string {
    const control = this.registerForm.get('firstName');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le prénom est requis';
      if (control.errors['minlength']) return 'Le prénom doit contenir au moins 2 caractères';
      if (control.errors['whitespace']) return 'Le prénom ne doit pas contenir de caractere speciaux inutiles';
    }
    return '';
  }

  get lastNameError(): string {
    const control = this.registerForm.get('name');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le nom est requis';
      if (control.errors['minlength']) return 'Le nom doit contenir au moins 2 caractères';
      if (control.errors['whitespace']) return 'Le nom ne doit pas contenir de caracteres speciaux inutiles';
    }
    return '';
  }

  get roleError():string {
    const control = this.registerForm.get('role');
      if (control?.errors && control.touched) {
        if (control.errors['required']) return 'Le role de l\'utilisateur est requis';
      }
    return '';
  }

  get usernameError(): string {
    const control = this.registerForm.get('username');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le nom d\'utilisateur est requis';
      if (control.errors['minlength']) return 'Le nom d\'utilisateur doit contenir au moins 2 caractères';
      if (control.errors['whitespace']) return 'Le nom d\'utilisateur ne doit pas contenir de caracteres speciaux inutiles';
    }
    return '';
  }

  get emailError(): string {
    const control = this.registerForm.get('email');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'L\'email est requis';
      if (control.errors['email']) return 'Format d\'email invalide';
    }
    return '';
  }

  get passwordError(): string {
    const control = this.registerForm.get('password');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'Le mot de passe est requis';
      if (control.errors['minlength']) return 'Le mot de passe doit contenir au moins 8 caractères';
      if (control.errors['pattern']) return 'Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial';
    }
    return '';
  }

  get confirmPasswordError(): string {
    const control = this.registerForm.get('confirmPassword');
    if (control?.errors && control.touched) {
      if (control.errors['required']) return 'La confirmation du mot de passe est requise';
      if (control.errors['passwordMismatch']) return 'Les mots de passe ne correspondent pas';
    }
    return '';
  }

  togglePasswordVisibility(field: 'password' | 'confirmPassword'): void {
    if (field === 'password') {
      this.showPassword = !this.showPassword;
    } else {
      this.showConfirmPassword = !this.showConfirmPassword;
    }
  }

  async onSubmit(): Promise<void> {
    if (this.registerForm.valid) {
      this.isSubmitting = true;

      try {
        // Structurer les données comme attendu par l'API
        const formData = {
          appUser: {
            username: this.registerForm.value.username,
            email: this.registerForm.value.email,
            password: this.registerForm.value.password,
            name: this.registerForm.value.name,
            firstName: this.registerForm.value.firstName
          },
          role: {
            name: this.registerForm.value.role
          }
        };

        console.log(formData);


        await this.authService.register(formData).toPromise();
        this.router.navigate(['/login'], {
          queryParams: { registered: 'success' }
        });
      } catch (error: any) {
        console.log('Erreur complète:',error.error);
        if (error.error.status === 500 && error.error?.detail === 'Email already in use') {
          // Vérifiez le code et le message d'erreur pour afficher le message approprié
          this.registerForm.get('email')?.setErrors({ emailExists: true });
        } else {
          console.log(error.status)
          console.error('Erreur lors de l\'inscription:', error.error);
        }      }
        finally {
        this.isSubmitting = false;
        }
    } else {
        if (!this.registerForm.get('acceptTerms')?.value) {
          this.termsError = 'Vous devez accepter les conditions générales d\'utilisation';
        }

      Object.keys(this.registerForm.controls).forEach(key => {
        const control = this.registerForm.get(key);
        if (control?.invalid) {
          control.markAsTouched();
        }
      });
    }
  }
}
