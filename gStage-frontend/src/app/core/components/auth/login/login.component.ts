import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss', '../auth.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage: string | null = null;


  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
   ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {}

  async onSubmit(): Promise<void> {
      if(this.loginForm.valid){
        this.errorMessage = null;
        this.isLoading = true;

        try {
          const formData = this.loginForm.value;
          await this.authService.login(formData).toPromise();
          
        } catch (error: any) {
          console.error('Erreur lors de la connexion');
          this.errorMessage = 'Nom d\'utilisateur ou mot de passe incorrect.';

        }finally {
          this.isLoading = false;
        }
      }
  }
}
