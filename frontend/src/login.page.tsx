import React, { ChangeEvent, FC } from "react";
import * as Yup from "yup";
import { useFormik } from "formik";
import { toast } from "react-toastify";
import { Input } from "@/components/input";
import { Button } from "@/components/button";

export const LoginPage: FC = () => {
  const formik = useFormik({
    initialValues: {
      password: "",
      username: "",
    },
    onSubmit: async ({ password, username }, formikHelpers): Promise<void> => {
      try {
        console.log([username, password]);
      } finally {
        formikHelpers.setSubmitting(false);
      }
    },
    validateOnMount: true,
    validationSchema: Yup.object({
      password: Yup.string()
        .min(6, "The minimum password length is 6 characters")
        .max(20, "The maximum password length is 20 characters")
        .required("Please enter a password"),
      username: Yup.string()
        .min(3, "The minimum username length is 3 characters")
        .max(20, "The maximum username length is 20 characters")
        .required("Please enter a username"),
    }),
  });

  const handleChange = (e: ChangeEvent<HTMLInputElement>): void => formik.handleChange(e);

  const handleSubmit = (): void => {
    if (Boolean(formik.errors.username)) {
      toast.error(formik.errors.username);
    } else if (Boolean(formik.errors.password)) {
      toast.error(formik.errors.password);
    } else {
      void formik.submitForm();
    }
  };

  return (
    <div className="h-screen">
      <div className="flex justify-center items-center flex-wrap h-full">
        <div className="sm:w-8/12 md:w-6/12 lg:w-4/12 xl:w-3/12">
          <form>
            <div className="mb-8">
              <Input
                type="text"
                className="w-full rounded-none py-5 px-5 text-md bg-white"
                placeholder="Username"
                name="username"
                onChange={handleChange}
              />
            </div>

            <div className="mb-8">
              <Input
                type="password"
                className="w-full rounded-none py-5 px-5 text-md bg-white"
                placeholder="Password"
                name="password"
                onChange={handleChange}
              />
            </div>

            <Button
              variant="outline"
              type="button"
              className="w-full border-solid rounded-none py-5 px-5 text-md bg-primary text-primary-foreground"
              onClick={handleSubmit}
            >
              SignIn or SignUp
            </Button>
          </form>
        </div>
      </div>
    </div>
  );
};
